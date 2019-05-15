/*
 * Copyright 2019 OpenFeign Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package feign.template;

import feign.support.Assert;
import feign.support.StringUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Chunk that represents an Expression that, adheres to RFC 6570, and will be resolved during
 * expansion.
 */
public abstract class Expression implements Chunk {

  static final String DEFAULT_DELIMITER = ",";
  private final List<String> variables = new ArrayList<>();
  private int limit;
  private boolean explode = false;
  private boolean includeName = false;

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

    /* check to see if the varspec ends in a (*), indicating that the values should be exploded */
    if (variableSpecification.endsWith("*")) {
      this.explode = true;

      /* strip the star */
      variableSpecification =
          variableSpecification.substring(0, variableSpecification.length() - 1);
    }

    if (variableSpecification.contains(Expressions.MULTIPLE_VALUE_DELIMITER)) {
      /* multiple variables are present in the spec */
      String[] variableSpecifications = variableSpecification
          .split(Expressions.MULTIPLE_VALUE_DELIMITER);
      this.variables.addAll(Arrays.asList(variableSpecifications));
    } else {
      this.variables.add(variableSpecification);
    }
    this.limit = -1;
  }

  Expression(String variableSpecification, boolean includeName) {
    this(variableSpecification);
    this.includeName = includeName;
  }

  /**
   * Expand this variables based on the value provided.
   *
   * @param variables to expand.
   * @return the expanded Expression value.
   */
  String expand(Map<String, ?> variables) {
    /* flag that manages a special condition where we have a list or map of values and the
     * result is empty.
     */
    boolean emptyListOrMap = false;

    StringBuilder builder = new StringBuilder();
    for (String variable : this.variables) {
      if (variables.containsKey(variable)) {
        Object value = variables.get(variable);
        if (value == null) {
          builder.append("undef");
        } else if (Iterable.class.isAssignableFrom(value.getClass())) {

          /* expand each item in the list */
          Iterable<?> list = (Iterable<?>) value;

          /* mark the list empty by checking the iterator */
          emptyListOrMap = !(list.iterator().hasNext());

          /* expand the list */
          this.expand(variable, list, builder);
        } else if (Map.class.isAssignableFrom(value.getClass())) {

          /* expand each key,value pair in the map */
          Map<?, ?> map = (Map<?, ?>) value;

          /* mark the map empty if empty */
          emptyListOrMap = map.isEmpty();

          /* expand the map */
          this.expand(variable, map, builder);
        } else {

          /* append the delimiter for this simple value */
          this.appendDelimiter(builder, this.getDelimiter());

          /* expand the simple value */
          this.expand(variable, value.toString(), builder);
        }
      } else {
        /* variable does not exist, undefined */
        builder.append("undef");
      }
    }

    /* remove any undefined values from the final map */
    String result = builder.toString();
    if (result.contains("undef")) {
      String pattern = "undef";
      result = result.replaceAll(pattern, "");

      /* special handling, if nothing is left, the entire string is undefined */
      if (StringUtils.isEmpty(result)) {
        return null;
      }
    }

    /* special case: don't prepend the prefix if we have an empty list or map */
    String prefix = (emptyListOrMap) ? "" : this.getPrefix();
    return prefix + result;
  }

  /**
   * Expand a list of values, resulting in a single value separated by the appropriate delimiter.
   *
   * @param name of the variable.
   * @param values to expand.
   * @param builder to append the values into.
   */
  private void expand(String name, Iterable<?> values, StringBuilder builder) {
    /* limits are not allowed on lists */
    if (this.limit >= 0) {
      /* limits are not allowed */
      throw new IllegalStateException(
          this.getValue() + ":Prefix Limits are not allowed on exploded composite or list values");
    }

    StringBuilder expanded = new StringBuilder();
    for (Object value : values) {
      String delimiter = (this.explode) ? this.getDelimiter() : DEFAULT_DELIMITER;
      this.appendDelimiter(expanded, delimiter);

      String result = this.expand(value);

      if (this.includeName && this.explode) {
        /* we need to include the name on every item when exploding lists */
        this.appendNamedResult(name, result, expanded);
      } else {
        /* append the value only */
        expanded.append(result);
      }
    }

    if (this.includeName && expanded.length() != 0 && !this.explode) {
      /* prepend the name in cases where we have not exploded the list */
      this.appendNamedResult(name, expanded, builder);
    } else {
      builder.append(expanded);
    }

  }

  /**
   * Expand an associative array a.k.a {@code Map}.
   *
   * @param name of the variable.
   * @param valueMap to expand.
   * @param builder to append the values into.
   */
  private void expand(String name, Map<?, ?> valueMap, StringBuilder builder) {
    /* limits are not allowed on associative arrays */
    if (this.limit >= 0) {
      /* limits are not allowed */
      throw new IllegalStateException(
          this.getValue() + ":Prefix Limits are not allowed on exploded composite or list values");
    }

    StringBuilder values = new StringBuilder();
    valueMap.forEach((key, value) -> {
      /* use the delimiter for the expression if exploded */
      String delimiter = (this.explode) ? this.getDelimiter() : DEFAULT_DELIMITER;
      appendDelimiter(values, delimiter);

      /* when this expression is explicitly 'exploded' we need to create key=value pairs */
      String keyValueDelimiter = (explode) ? "=" : DEFAULT_DELIMITER;
      values.append(encode(key.toString()))
          .append(keyValueDelimiter)
          .append(expand(value.toString()));
    });

    if (this.includeName && values.length() != 0 && !this.explode) {
      this.appendNamedResult(name, values, builder);
    } else {
      builder.append(values);
    }
  }

  /**
   * Expand a single value.
   *
   * @param name of the variable.
   * @param value to expand.
   * @param builder to append values into.
   */
  private void expand(String name, String value, StringBuilder builder) {
    /* expand the value */
    String result = this.expand(value);

    if (this.includeName) {
      /* prepend the name */
      this.appendNamedResult(name, result, builder);
    } else {
      builder.append(result);
    }
  }

  /**
   * Expands a single object into a String, limited if necessary.  This implementation uses {@link
   * Object#toString()}.
   *
   * @param value to expand.
   * @return the expanded value.
   */
  private String expand(Object value) {
    if (value == null) {
      return null;
    }

    String result = value.toString();

    if (this.limit > 0) {
      int end = this.limit;

      /* guard against aggressive limits */
      if (this.limit > result.length()) {
        end = result.length();
      }

      result = result.substring(0, end);
    }

    return this.encode(result);
  }

  /**
   * Appends the specified delimiter to the builder, if necessary.
   *
   * @param builder to append to.
   * @param delimiter to append.
   */
  protected void appendDelimiter(StringBuilder builder, String delimiter) {
    if (builder.length() != 0) {
      /* only append if values are already present */
      builder.append(delimiter);
    }
  }

  /**
   * Appends the result in a name-value pair format.
   *
   * @param name of the result.
   * @param result value.
   * @param builder to append the named result to.
   */
  protected void appendNamedResult(String name, Object result, StringBuilder builder) {
    /* prepend the name */
    builder.append(this.encode(name))
        .append("=")
        .append(result);
  }

  /**
   * Encode the value, using this expressions filter.
   *
   * @param value to encode.
   * @return a pct-encoding String.
   */
  String encode(String value) {
    if (!this.isPctEncoded(value)) {
      byte[] data = value.getBytes(StandardCharsets.UTF_8);

      try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
        for (byte b : data) {
          if (this.isCharacterAllowed((char) b)) {
            bos.write(b);
          } else {
            UriUtils.pctEncode(b, bos);
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
    return UriUtils.isPctEncoded(value);
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
  protected abstract String getDelimiter();

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
    return Collections.unmodifiableList(this.variables);
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
   * Set a limit to the number of characters to included in an exploded value.
   *
   * @param limit character limit.
   * @throws IllegalStateException if a limit is applied to an exploded or prefixed value.
   */
  void setLimit(int limit) {
    this.limit = limit;
  }

  /**
   * If an explode modifier is present.
   *
   * @return the explode flag.
   */
  boolean hasExplodeModifier() {
    return explode;
  }

  @Override
  public String getValue() {
    if (this.limit > 0) {
      return "{" + String.join(",", this.variables) + ((this.explode) ? "*" : "") + ":"
          + this.limit + "}";
    }
    return "{" + String.join(",", this.variables) + ((this.explode) ? "*" : "") + "}";
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
