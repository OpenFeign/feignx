package feign.template;

import feign.support.StringUtils;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Uri Template based on RFC 6570.
 */
public class UriTemplate {

  private final List<Chunk> chunks = new ArrayList<>();

  /**
   * Creates a ne Uri Template.
   *
   * @param uri containing the template.
   * @return a new UriTemplate instance.
   * @throws IllegalArgumentException if the provided uri is not valid.
   */
  public static UriTemplate create(String uri) {
    return new UriTemplate(uri);
  }

  /**
   * Create a new UriTemplate instance.
   *
   * @param uri to parse.
   */
  private UriTemplate(String uri) {
    this.parse(uri);
  }

  /**
   * Expand the template.
   *
   * @param variables with possible expression values.
   * @return a valid, expanded URI.
   */
  public URI expand(Map<String, ?> variables) {
    StringBuilder expanded = new StringBuilder();
    for (Chunk chunk : chunks) {
      if (Expression.class.isAssignableFrom(chunk.getClass())) {
        String result = this.expand((Expression) chunk, variables);

        /* ignore undefined expressions */
        if (result != null) {
          expanded.append(result);
        }
      } else {
        expanded.append(chunk.getValue());
      }
    }
    return URI.create(expanded.toString());
  }

  /**
   * Expand the given Expression.
   *
   * @param expression to expand.
   * @param variables containing possible expanded values.
   * @return the expanded value or {@literal null} if the expression variable is undefined.
   */
  private String expand(Expression expression, Map<String, ?> variables) {
    /* look for the expression value in the variable map */
    String name = expression.getVariable();
    if (variables.containsKey(name)) {
      return expression.expand(variables.get(name));
    }
    return null;
  }

  /**
   * Parse the URI.
   *
   * @param uri to parse.
   */
  private void parse(String uri) {
    /* take the uri provided, converting temporarily into a URI so we can parse
     * each section of the uri separately, as there are different rules on how
     * an expression should expand based on it's position in the URI
     */
    String encodedUri = uri.replaceAll("\\{", "%7B")
        .replaceAll("}", "%7D");
    URI templateUri = URI.create(encodedUri);

    /* parse each section of the uri, creating expressions as necessary */
    this.parseSegment(templateUri.getScheme());
    this.parseSegment(templateUri.getAuthority());
    this.parseSegment(templateUri.getPath());
    this.parseSegment(templateUri.getQuery());
    this.parseSegment(templateUri.getFragment());
  }

  /**
   * Parse the Segment of the URI.
   *
   * @param segment to parse.
   */
  private void parseSegment(String segment) {
    if (StringUtils.isNotEmpty(segment)) {
      ChunkTokenizer tokenizer = new ChunkTokenizer(segment);
      while (tokenizer.hasNext()) {
        String chunk = tokenizer.next();
        if (Expressions.isExpression(chunk)) {
          Expression expression = Expressions.create(chunk);
          this.chunks.add(expression);
        } else {
          this.chunks.add(new Literal(chunk));
        }
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    this.chunks.forEach(chunk -> sb.append(chunk.getValue()));
    return sb.toString();
  }

  /**
   * Splits a Uri into Chunks that exists inside and outside of an expression, delimited by curly
   * braces "{}". Nested expressions are treated as literals, for example "foo{bar{baz}}" will be
   * treated as "foo, {bar{baz}}". Inspired by Apache CXF Jax-RS.
   */
  static class ChunkTokenizer {

    private List<String> tokens = new ArrayList<>();
    private int index;

    ChunkTokenizer(String template) {
      boolean outside = true;
      int level = 0;
      int lastIndex = 0;
      int idx;

      /* loop through the template, character by character */
      for (idx = 0; idx < template.length(); idx++) {
        if (template.charAt(idx) == '{') {
          /* start of an expression */
          if (outside) {
            /* outside of an expression */
            if (lastIndex < idx) {
              /* this is the start of a new token */
              tokens.add(template.substring(lastIndex, idx));
            }
            lastIndex = idx;

            /*
             * no longer outside of an expression, additional characters will be treated as in an
             * expression
             */
            outside = false;
          } else {
            /* nested braces, increase our nesting level */
            level++;
          }
        } else if (template.charAt(idx) == '}' && !outside) {
          /* the end of an expression */
          if (level > 0) {
            /*
             * sometimes we see nested expressions, we only want the outer most expression
             * boundaries.
             */
            level--;
          } else {
            /* outermost boundary */
            if (lastIndex < idx) {
              /* this is the end of an expression token */
              tokens.add(template.substring(lastIndex, idx + 1));
            }
            lastIndex = idx + 1;

            /* outside an expression */
            outside = true;
          }
        }
      }
      if (lastIndex < idx) {
        /* grab the remaining chunk */
        tokens.add(template.substring(lastIndex, idx));
      }
    }

    boolean hasNext() {
      return this.tokens.size() > this.index;
    }

    String next() {
      if (hasNext()) {
        return this.tokens.get(this.index++);
      }
      throw new IllegalStateException("No More Elements");
    }
  }

}
