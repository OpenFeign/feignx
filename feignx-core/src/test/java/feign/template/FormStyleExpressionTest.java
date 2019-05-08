package feign.template;

class FormStyleExpressionTest extends StyleExpressionTest {

  @Override
  protected Expression getExpression(String variableSpecification, int limit) {
    Expression expression = new FormStyleExpression(variableSpecification);
    expression.setLimit(limit);
    return expression;
  }
}