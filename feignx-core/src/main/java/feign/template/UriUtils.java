package feign.template;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * Utility Methods for dealing with URI Character classifications.
 */
@SuppressWarnings("WeakerAccess")
public class UriUtils {

  private static final Pattern PCT_ENCODED_PATTERN = Pattern.compile("%[0-9A-Fa-f][0-9A-Fa-f]");

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

  /**
   * Encode the value, using this expressions filter.
   *
   * @param value to encode.
   * @return a pct-encoding String.
   */
  public static String encode(String value) {
    if (!isPctEncoded(value)) {
      byte[] data = value.getBytes(StandardCharsets.UTF_8);

      try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
        for (byte b : data) {
          if (isUnreserved((char) b)) {
            bos.write(b);
          } else {
            pctEncode(b, bos);
          }
        }
        return new String(bos.toByteArray());
      } catch (IOException ioe) {
        throw new IllegalStateException("Error occurred during encoding of the uri: "
            + ioe.getMessage(), ioe);
      }
    }
    return value;
  }

  /**
   * Determines if the value is already pct-encoded.
   *
   * @param value to check.
   * @return {@literal true} if the value is already pct-encoded, {@literal false} otherwise.
   */
  public static boolean isPctEncoded(String value) {
    return PCT_ENCODED_PATTERN.matcher(value).find();
  }

  /**
   * PCT Encodes the data provided, into the provided output stream.
   *
   * @param data to encode.
   * @param encodedOutputStream to receive the encoded data.
   */
  public static void pctEncode(byte data, ByteArrayOutputStream encodedOutputStream) {
    encodedOutputStream.write('%');
    char hex1 = Character.toUpperCase(Character.forDigit((data >> 4) & 0xF, 16));
    char hex2 = Character.toUpperCase(Character.forDigit(data & 0xF, 16));
    encodedOutputStream.write(hex1);
    encodedOutputStream.write(hex2);
  }

}
