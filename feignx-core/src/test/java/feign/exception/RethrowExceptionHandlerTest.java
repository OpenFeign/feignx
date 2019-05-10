package feign.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import feign.exception.ExceptionHandler.RethrowExceptionHandler;
import org.junit.jupiter.api.Test;

class RethrowExceptionHandlerTest {

  @Test
  void exceptions_shouldBeThrown() {
    ExceptionHandler exceptionHandler = new RethrowExceptionHandler();

    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> exceptionHandler.accept(new FeignException("Test", "method")));

    assertThat(exception.getCause()).isInstanceOf(FeignException.class);
  }

}