package feign.template;

class FragmentExpressionTest extends ReservedExpressionTest {

  @Override
  protected Expression getExpression(String variableSpecification, int limit) {
    FragmentExpression fragmentExpression = new FragmentExpression(variableSpecification);
    fragmentExpression.setLimit(limit);
    return fragmentExpression;

  }
}