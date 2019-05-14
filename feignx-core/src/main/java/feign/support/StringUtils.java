package feign.support;

/**
 * String Utility Methods.
 */
public class StringUtils {

  /**
   * Determines if the provided String is not empty or {@literal null}.
   *
   * @param value to evaluate.
   * @return {@literal true} if the value is not empty or {@literal null}, {@literal false}
   *        otherwise.
   */
  public static boolean isNotEmpty(String value) {
    return value != null && !value.isEmpty();
  }

  /**
   * Determines if the provided String is empty or {@literal null}.
   *
   * @param value to evaluate.
   * @return {@literal true} if the value is empty or {@literal null}, {@literal false} otherwise.
   */
  public static boolean isEmpty(String value) {
    return value == null || value.isEmpty();
  }

}
