package feign.http;

import feign.Header;
import feign.Request;
import feign.RequestOptions;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Http Request Model.
 */
public final class HttpRequest implements Request {

  private URI uri;
  private HttpMethod method;
  private List<Header> headers = new ArrayList<>();
  private RequestOptions options;
  private byte[] content;

  /**
   * Creates a new empty HttpRequest.
   */
  HttpRequest() {
    super();
  }

  /**
   * Creates a new HttpRequest.
   *
   * @param uri for the request.
   * @param method for the request.
   * @param headers to include on the request.
   * @param options for the request.
   * @param content to include in the request.
   */
  public HttpRequest(URI uri, HttpMethod method, Collection<Header> headers, RequestOptions options,
      byte[] content) {
    this.uri = uri;
    this.method = method;
    if (headers != null) {
      this.headers.addAll(headers);
    }
    this.options = (options == null) ? RequestOptions.builder().build() : options;
    this.content = content;
  }

  /**
   * URI of the Request.
   *
   * @return request uri.
   */
  @Override
  public URI uri() {
    return this.uri;
  }

  /**
   * Request Content.  Can be {@literal null}.
   *
   * @return the request content, if any.
   */
  @Override
  public byte[] content() {
    return this.content;
  }

  /**
   * Request Content size.
   *
   * @return content size.
   */
  @Override
  public int contentLength() {
    return (this.content != null) ? this.content.length : 0;
  }

  /**
   * Http Method for the Request.
   *
   * @return the http method.
   */
  @Override
  public HttpMethod method() {
    return this.method;
  }

  /**
   * Headers for the Request.
   *
   * @return an array of Request Headers.
   */
  @Override
  public List<Header> headers() {
    return this.headers;
  }

  /**
   * Options for the Request.
   *
   * @return request options.
   */
  @Override
  public RequestOptions options() {
    return this.options;
  }

  @Override
  public String toString() {
    return "HttpRequest [" + "uri=" + uri
        + ", method=" + method
        + ", headers=" + headers
        + ", options=" + options
        + "]";
  }
}
