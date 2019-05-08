package feign.template;

/**
 * Fragment based expression.  Assumes the same capability as a reserved expression, but prefixed
 * with a {@literal #}
 */
public class FragmentExpression extends ReservedExpression {

  public FragmentExpression(String variableSpecification) {
    super(variableSpecification);
  }

  @Override
  protected String getPrefix() {
    return "#";
  }

}
