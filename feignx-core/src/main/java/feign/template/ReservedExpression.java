package feign.template;

/**
 * Expression that allows for characters in the Reserved to be included, without encoding.
 */
public class ReservedExpression extends SimpleExpression {

  ReservedExpression(String variableSpecification) {
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
