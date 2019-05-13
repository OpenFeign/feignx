package feign.exception;

/**
 * Base Exception for all internal errors.
 */
public class FeignException extends RuntimeException {

  private String method;

  /**
   * Creates a new Feign Exception.
   *
   * @param message for the exception.
   * @param method name in which the exception occurred.
   */
  public FeignException(String message, String method) {
    super(message);
    this.method = method;
  }

  /**
   * Creates a new Feign Exception.
   *
   * @param message for the exception.
   * @param cause of the exception.
   * @param method name in which the exception occurred.
   */
  public FeignException(String message, Throwable cause, String method) {
    super(message, cause);
    this.method = method;
  }

  /**
   * Method name.
   * @return the method name.
   */
  public String getMethod() {
    return method;
  }
}
