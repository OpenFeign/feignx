package feign.exception;

public abstract class FeignException extends RuntimeException {

  private String method;

  public FeignException(String message, String method) {
    super(message);
    this.method = method;
  }

  public FeignException(String message, Throwable cause, String method) {
    super(message, cause);
    this.method = method;
  }

  public String getMethod() {
    return method;
  }
}
