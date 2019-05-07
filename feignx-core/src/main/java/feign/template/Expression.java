package feign.template;

import feign.support.Assert;
import feign.support.StringUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

/**
 * Chunk that represents an Expression that, adheres to RFC 6570, and will be resolved during
 * expansion.
 */
public abstract class Expression implements Chunk {

  private static final Pattern PCT_ENCODED_PATTERN = Pattern.compile("%[0-9A-Fa-f][0-9A-Fa-f]");
  private static final String MULTIPLE_VALUE_DELIMITER = ",";
  private final Set<String> variables = new LinkedHashSet<>();
  private int limit;
  private boolean explode = false;

  /**
   * Creates a new Expression.
   *
   * @param variableSpecification template.
   */
  Expression(String variableSpecification) {
    Assert.isNotEmpty(variableSpecification, "variable is required.");

    /* remove the leading and trailing braces if necessary */
    if (variableSpecification.startsWith("{")) {
      variableSpecification = variableSpecification
          .substring(1, variableSpecification.length() - 1);
    }

    if (variableSpecification.contains(MULTIPLE_VALUE_DELIMITER)) {
      /* multiple variables are present in the spec */
      String[] variableSpecifications = variableSpecification.split(MULTIPLE_VALUE_DELIMITER);
      this.variables.addAll(Arrays.asList(variableSpecifications));
    } else {
      this.variables.add(variableSpecification);
    }
    this.limit = -1;
  }

  /**
   * Creates a new Expression, with a prefix limiting the amount of characters to include during
   * expansion.
   *
   * @param variables template.
   * @param limit regular variables.
   */
  Expression(String variables, int limit) {
    this(variables);
    this.limit = limit;
  }

  /**
   * Creates a new Expression, with a prefix limiting the amount of characters to include during
   * expansion.
   *
   * @param variables template.
   * @param limit regular variables.
   * @param explode flag indicating that if this expression resolves to a list of values, it should
   * be "exploded" accordingly.
   */
  Expression(String variables, int limit, boolean explode) {
    this(variables);
    this.limit = limit;
    this.explode = explode;
  }

  /**
   * Expand this variables based on the value provided.
   *
   * @param variables to expand.
   * @return the expanded Expression value.
   */
  String expand(Map<String, ?> variables) {
    StringBuilder builder = new StringBuilder();
    for (String variable : this.variables) {
      if (variables.containsKey(variable)) {
        Object value = variables.get(variable);

        if (Iterable.class.isAssignableFrom(value.getClass())) {
          this.expand((Iterable<?>) value, builder);
        } else if (Map.class.isAssignableFrom(value.getClass())) {
          this.expand((Map<?, ?>) value, builder);
        } else {
          builder.append(this.getPrefix());
          this.appendDelimiter(builder);
          builder.append(this.expand(value));
        }
      }
    }
    return builder.toString();
  }

  /**
   * Expand a list of values, resulting in a single value separated by the appropriate delimiter.
   *
   * @param values to expand.
   * @param builder to append the values into.
   */
  private void expand(Iterable<?> values, StringBuilder builder) {
    builder.append(this.getPrefix());
    for (Object value : values) {
      this.appendDelimiter(builder);
      builder.append(this.expand(value));
    }
  }

  private void expand(Map<?, ?> valueMap, StringBuilder builder) {
    builder.append(this.getPrefix());
    valueMap.forEach((key, value) -> {
      appendDelimiter(builder);

      /* when this expression is explicitly 'exploded' we need to create key=value pairs */
      char delimiter = (explode) ? '=' : getListDelimiter();
      builder.append(encode(key.toString()))
          .append(delimiter)
          .append(encode(value.toString()));
    });

  }

  private String expand(Object value) {
    String result = value.toString();

    if (this.limit > 0) {
      result = result.substring(0, this.limit);
    }
    return this.encode(result);
  }

  private void appendDelimiter(StringBuilder builder) {
    if (builder.length() != 0) {
      builder.append(this.getListDelimiter());
    }
  }

  /**
   * Encode the value, using this expressions filter.
   *
   * @param value to encode.
   * @return a pct-encoding String.
   */
  private String encode(String value) {
    if (!this.isPctEncoded(value)) {
      byte[] data = value.getBytes(StandardCharsets.UTF_8);

      try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
        for (byte b : data) {
          if (this.isCharacterAllowed((char) b)) {
            bos.write(b);
          } else {
            pctEncode(b, bos);
          }
        }
        return new String(bos.toByteArray());
      } catch (IOException ioe) {
        throw new IllegalStateException("Error occurred during encoding of the uri: "
            + ioe.getMessage(), ioe);
      }
    }
    return value;
  }

  /**
   * Determines if the value is already pct-encoded.
   *
   * @param value to check.
   * @return {@literal true} if the value is already pct-encoded, {@literal false} otherwise.
   */
  private boolean isPctEncoded(String value) {
    return PCT_ENCODED_PATTERN.matcher(value).find();
  }


  /**
   * PCT Encodes the data provided, into the provided output stream.
   *
   * @param data to encode.
   * @param encodedOutputStream to receive the encoded data.
   */
  private void pctEncode(byte data, ByteArrayOutputStream encodedOutputStream) {
    encodedOutputStream.write('%');
    char hex1 = Character.toUpperCase(Character.forDigit((data >> 4) & 0xF, 16));
    char hex2 = Character.toUpperCase(Character.forDigit(data & 0xF, 16));
    encodedOutputStream.write(hex1);
    encodedOutputStream.write(hex2);
  }

  /**
   * Determines if the provided character is allowed in the expanded value.
   *
   * @param character to evaluate.
   * @return {@literal true} if the character can remain, {@literal false} otherwise..
   */
  protected abstract boolean isCharacterAllowed(char character);

  /**
   * The character to use when separating lists of values.
   *
   * @return the list delimiter.
   */
  protected abstract char getListDelimiter();

  /**
   * The character to prefix each expanded value.
   *
   * @return the prefix character, can be {@literal null}
   */
  protected abstract String getPrefix();

  /**
   * Variable name for this expression.
   *
   * @return expression variables.
   */
  Collection<String> getVariables() {
    return Collections.unmodifiableSet(this.variables);
  }

  /**
   * The number of characters to limit the expanded value to.
   *
   * @return the expanded character limit.
   */
  int getLimit() {
    return limit;
  }

  /**
   * Determines if this expression should explode list or collection based values.
   *
   * @return if this expression should explode list or collection values.
   */
  boolean shouldExplode() {
    return this.explode;
  }

  @Override
  public String getValue() {
    if (this.limit > 0) {
      return "{" + this.variables + ":" + this.limit + "}";
    }
    return "{" + this.variables + "}";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Expression)) {
      return false;
    }
    Expression that = (Expression) obj;
    return variables.equals(that.variables);
  }

  @Override
  public int hashCode() {
    return Objects.hash(variables);
  }

  @Override
  public String toString() {
    return "Expression [" + "variables='" + variables + "'" + ", limit=" + limit + "]";
  }
}
