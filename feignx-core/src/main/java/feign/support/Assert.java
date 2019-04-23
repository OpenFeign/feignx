package feign.support;

import java.util.function.Predicate;

public final class Assert {

  public static void isNotNull(Object value, String message) {
    if (value == null) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void isNotEmpty(String value, String message) {
    if (value != null && !value.trim().isEmpty()) {
      throw new IllegalArgumentException(message);
    }
  }

  public static <T> void isTrue(T value, Predicate<T> expression, String message) {
     if (!expression.test(value)){
       throw new IllegalStateException(message);
     }
  }

  public static <T> void isFalse(T value, Predicate<T> expression, String message) {
    isTrue(value, expression.negate(), message);
  }
}