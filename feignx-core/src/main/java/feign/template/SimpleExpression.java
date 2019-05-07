package feign.template;

public class SimpleExpression extends Expression {

  public SimpleExpression(String variableSpecification) {
    super(variableSpecification);
  }

  @Override
  protected boolean isCharacterAllowed(char character) {
    /* only unreserved and our delimiter are allowed */
    return UriUtils.isUnreserved(character);
  }


  @Override
  protected char getListDelimiter() {
    return ',';
  }

  @Override
  protected String getPrefix() {
    return "";
  }
}
