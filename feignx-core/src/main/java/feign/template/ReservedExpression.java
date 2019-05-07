package feign.template;

public class ReservedExpression extends SimpleExpression {

  public ReservedExpression(String variableSpecification) {
    super(variableSpecification);
  }

  @Override
  protected boolean isCharacterAllowed(char character) {
    return super.isCharacterAllowed(character) || UriUtils.isReserved(character);
  }

  @Override
  protected String getPrefix() {
    return super.getPrefix();
  }
}
