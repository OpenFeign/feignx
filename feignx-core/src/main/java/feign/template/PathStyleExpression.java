package feign.template;

import feign.support.StringUtils;

/**
 * Expression that use the semi-colon {@code ;} character as a prefix and exploded delimiter,
 * allowing for expansion of path parameters.
 */
public class PathStyleExpression extends SimpleExpression {

  private static final String SEMI = ";";

  PathStyleExpression(String variableSpecification) {
    super(variableSpecification, true);
  }

  @Override
  protected String getDelimiter() {
    return SEMI;
  }

  @Override
  protected String getPrefix() {
    return SEMI;
  }

  @Override
  protected void appendNamedResult(String name, Object result, StringBuilder builder) {
    /* append the name */
    builder.append(this.encode(name));

    /* check the result, first to see if it's 'empty' */
    if (result instanceof String) {
      if (StringUtils.isNotEmpty((String) result)) {
        /* append the equals */
        builder.append("=");
      }
    } else {
      /* always append otherwise */
      builder.append("=");
    }

    /* append the result */
    builder.append(result);
  }
}
