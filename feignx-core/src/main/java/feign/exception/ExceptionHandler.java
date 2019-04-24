package feign.exception;

import java.util.function.Consumer;

public interface ExceptionHandler extends Consumer<Throwable> {

  /**
   * Throws a new RuntimeException based on the exception received.
   *
   * @param throwable to throw again.
   */
  @Override
  default void accept(Throwable throwable) {
    /* always rethrow */
    throw new RuntimeException(throwable);
  }
}
