package feign.template;

/**
 * Expression that expands values using a Query String {@code ?} and {@code &} style,
 * allowing for expansion of query parameters.
 */
public class FormStyleExpression extends SimpleExpression {

  private static final String QUESTION = "?";
  static final String AMPERSAND = "&";

  FormStyleExpression(String variableSpecification) {
    super(variableSpecification, true);
  }

  @Override
  protected String getDelimiter() {
    return AMPERSAND;
  }

  @Override
  protected String getPrefix() {
    return QUESTION;
  }

}
