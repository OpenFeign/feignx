package feign.template;

/**
 * Expression that use the slash {@code /} character as a prefix and exploded delimiter, allowing
 * for expansion of path segments.
 */
public class PathSegmentExpression extends SimpleExpression {

  private static final String SLASH = "/";

  PathSegmentExpression(String variableSpecification) {
    super(variableSpecification);
  }

  @Override
  protected String getDelimiter() {
    return SLASH;
  }

  @Override
  protected String getPrefix() {
    return SLASH;
  }
}
