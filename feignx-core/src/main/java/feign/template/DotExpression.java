package feign.template;

/**
 * Expression that uses the dot {@code .} character as a prefix and exploded delimiter, allowing
 * for expansion of domain names and other dot like values on a URI.
 */
public class DotExpression extends SimpleExpression {

  private static final String DOT = ".";

  DotExpression(String variableSpecification) {
    super(variableSpecification);
  }

  @Override
  protected String getPrefix() {
    return DOT;
  }

  @Override
  protected String getDelimiter() {
    return DOT;
  }
}
