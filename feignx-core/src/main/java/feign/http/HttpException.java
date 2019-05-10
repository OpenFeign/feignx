package feign.http;

import feign.Client;
import feign.Request;
import java.util.Optional;

/**
 * An exception that occurred during a {@link Client} operation.
 */
public class HttpException extends RuntimeException {

  private HttpRequest request;
  private HttpResponse response;

  /**
   * Creates a new Http Exception.
   *
   * @param message for the exception.
   * @param cause of the exception.
   */
  public HttpException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Creates a new Http Exception.
   *
   * @param message for the exception.
   * @param cause of the exception.
   * @param request that was attempted.
   */
  public HttpException(String message, Throwable cause, HttpRequest request) {
    super(message, cause);
    this.request = request;
  }

  /**
   * Creates a new Http Exception.
   *
   * @param message for the exception.
   * @param cause of the exception.
   * @param request that was attempted.
   * @param response that was received.
   */
  public HttpException(String message, Throwable cause, HttpRequest request,
      HttpResponse response) {
    super(message, cause);
    this.request = request;
    this.response = response;
  }

  public Optional<HttpRequest> getRequest() {
    return Optional.of(this.request);
  }

  public Optional<HttpResponse> getResponse() {
    return Optional.of(this.response);
  }


}
