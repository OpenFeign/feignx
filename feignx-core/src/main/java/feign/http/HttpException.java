package feign.http;

import feign.exception.FeignException;
import java.util.Optional;

/**
 * An exception that occurred during a {@link Client} operation.
 */
public abstract class HttpException extends FeignException {

  private final int status;
  private Request request;
  private Response response;

  public HttpException(String message, String method, int status) {
    super(message, method);
    this.status = status;
  }

  public HttpException(String message, Throwable cause, String method, int status) {
    super(message, cause, method);
    this.status = status;
  }

  public HttpException(String message, String method, int status, Request request,
      Response response) {
    super(message, method);
    this.status = status;
    this.request = request;
    this.response = response;
  }

  public HttpException(String message, Throwable cause, String method, int status,
      Request request, Response response) {
    super(message, cause, method);
    this.status = status;
    this.request = request;
    this.response = response;
  }

  public int getStatus() {
    return status;
  }

  public Optional<Request> getRequest() {
    return Optional.of(this.request);
  }

  public Optional<Response> getResponse() {
    return Optional.of(this.response);
  }


}
