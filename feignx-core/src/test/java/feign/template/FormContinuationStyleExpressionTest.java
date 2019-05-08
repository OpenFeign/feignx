package feign.template;

class FormContinuationStyleExpressionTest extends FormStyleExpressionTest {

  @Override
  protected Expression getExpression(String variableSpecification, int limit) {
    Expression expression = new FormContinuationStyleExpression(variableSpecification);
    expression.setLimit(limit);
    return expression;
  }
}