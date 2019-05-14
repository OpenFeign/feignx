package feign;

import java.util.function.Consumer;

/**
 * Consumer responsible for processing any Exceptions that occur during method processing.
 */
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

  /**
   * Exception Handler that wraps and throws any exceptions.
   */
  class RethrowExceptionHandler implements ExceptionHandler {

  }
}
