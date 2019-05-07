package feign.template;

public class UriUtils {

  public static boolean isUnreserved(char character) {
    return isAlpha(character) || isDigit(character) || character == '-' || character == '.'
        || character == '_' || character == '~';
  }

  public static boolean isAlpha(char character) {
    return ((character >= 'a' && character <= 'z') || (character >= 'A' && character <= 'Z'));
  }

  public static boolean isDigit(char character) {
    return (character >= '0' && character <= '9');
  }

  public static boolean isReserved(char character) {
    return isGeneralDelimiter(character) || isSubDelimiter(character);
  }

  public static boolean isGeneralDelimiter(char character) {
    return character == ':' || character == '/' || character == '?' || character == '#'
        || character == '[' || character == ']' || character == '@';
  }

  public static boolean isSubDelimiter(char character) {
    return character == '!' || character == '$' || character == '&' || character == '\''
        || character == '(' || character == ')' || character == '*' || character == '+'
        || character == ',' || character == ';' || character == '=';
  }

}
