package feign.template;

class SimpleExpressionTest extends ExpressionTest {

  @Override
  protected Expression getExpression(String variableSpecification, int limit) {
    Expression expression = new SimpleExpression(variableSpecification);
    expression.setLimit(limit);
    return expression;
  }
}