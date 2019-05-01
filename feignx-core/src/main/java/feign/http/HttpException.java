package feign.http;

import feign.Client;
import java.util.Optional;

/**
 * An exception that occurred during a {@link Client} operation.
 */
public class HttpException extends RuntimeException {

  private Request request;
  private Response response;

  public HttpException(String message) {
    super(message);
  }

  public HttpException(String message, Throwable cause) {
    super(message, cause);
  }

  public HttpException(Throwable cause) {
    super(cause);
  }

  public HttpException(String message, Request request) {
    super(message);
    this.request = request;
  }

  public HttpException(String message, Throwable cause, Request request) {
    super(message, cause);
    this.request = request;
  }

  public HttpException(Throwable cause, Request request) {
    super(cause);
    this.request = request;
  }

  public HttpException(String message, Request request, Response response) {
    super(message);
    this.request = request;
    this.response = response;
  }

  public HttpException(String message, Throwable cause, Request request, Response response) {
    super(message, cause);
    this.request = request;
    this.response = response;
  }

  public HttpException(Throwable cause, Request request, Response response) {
    super(cause);
    this.request = request;
    this.response = response;
  }

  public Optional<Request> getRequest() {
    return Optional.of(this.request);
  }

  public Optional<Response> getResponse() {
    return Optional.of(this.response);
  }


}
