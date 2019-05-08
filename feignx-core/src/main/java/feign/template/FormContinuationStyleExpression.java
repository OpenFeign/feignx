package feign.template;

/**
 * Expression that expands values that represent the continuation of a Query String,
 * allowing for expansion of additional query parameters.
 */
public class FormContinuationStyleExpression extends FormStyleExpression {

  FormContinuationStyleExpression(String variableSpecification) {
    super(variableSpecification);
  }

  @Override
  protected String getPrefix() {
    return AMPERSAND;
  }
}
