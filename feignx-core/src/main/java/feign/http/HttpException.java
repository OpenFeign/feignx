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

  public HttpException(String message) {
    super(message);
  }

  public HttpException(String message, Throwable cause) {
    super(message, cause);
  }

  public HttpException(Throwable cause) {
    super(cause);
  }

  public HttpException(String message, HttpRequest request) {
    super(message);
    this.request = request;
  }

  public HttpException(String message, Throwable cause, HttpRequest request) {
    super(message, cause);
    this.request = request;
  }

  public HttpException(Throwable cause, HttpRequest request) {
    super(cause);
    this.request = request;
  }

  public HttpException(String message, HttpRequest request, HttpResponse response) {
    super(message);
    this.request = request;
    this.response = response;
  }

  public HttpException(String message, Throwable cause, HttpRequest request, HttpResponse response) {
    super(message, cause);
    this.request = request;
    this.response = response;
  }

  public HttpException(Throwable cause, HttpRequest request, HttpResponse response) {
    super(cause);
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
