package feign.template;

/**
 * Utility Methods for dealing with URI Character classifications.
 */
@SuppressWarnings("WeakerAccess")
public class UriUtils {

  /**
   * Determines if the provided character is an unreserved character as defined in RFC 6570.
   *
   * @param character to evaluate.
   * @return {@literal true} if the value is an unreserved character or {@literal false} otherwise.
   */
  public static boolean isUnreserved(char character) {
    return isAlpha(character) || isDigit(character) || character == '-' || character == '.'
        || character == '_' || character == '~';
  }

  /**
   * Determines if the provided character is alphabetic as defined in RFC 6570.
   *
   * @param character to evaluate.
   * @return {@literal true} if the value is alphabetic or {@literal false} otherwise.
   */
  public static boolean isAlpha(char character) {
    return ((character >= 'a' && character <= 'z') || (character >= 'A' && character <= 'Z'));
  }

  /**
   * Determines if the provided character is a digit as defined in RFC 6570.
   *
   * @param character to evaluate.
   * @return {@literal true} if the value is a digit or {@literal false} otherwise.
   */
  public static boolean isDigit(char character) {
    return (character >= '0' && character <= '9');
  }

  /**
   * Determines if the provided character is a reserved character as defined in RFC 6570.
   *
   * @param character to evaluate.
   * @return {@literal true} if the value is a reserved character or {@literal false} otherwise.
   */
  public static boolean isReserved(char character) {
    return isGeneralDelimiter(character) || isSubDelimiter(character);
  }

  /**
   * Determines if the provided character is a general delimiter as defined in RFC 6570.
   *
   * @param character to evaluate.
   * @return {@literal true} if the value is a general delimiter or {@literal false} otherwise.
   */
  public static boolean isGeneralDelimiter(char character) {
    return character == ':' || character == '/' || character == '?' || character == '#'
        || character == '[' || character == ']' || character == '@';
  }

  /**
   * Determines if the provided character is a sub-delimiter as defined in RFC 6570.
   *
   * @param character to evaluate.
   * @return {@literal true} if the value is a sub-delimiter or {@literal false} otherwise.
   */
  public static boolean isSubDelimiter(char character) {
    return character == '!' || character == '$' || character == '&' || character == '\''
        || character == '(' || character == ')' || character == '*' || character == '+'
        || character == ',' || character == ';' || character == '=';
  }

}
