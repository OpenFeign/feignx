package feign.template;

public class SimpleExpression extends Expression {

  SimpleExpression(String variable) {
    super(variable);
  }

  SimpleExpression(String variable, int limit) {
    super(variable, limit);
  }

  public SimpleExpression(String variables, int limit, boolean explode) {
    super(variables, limit, explode);
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
