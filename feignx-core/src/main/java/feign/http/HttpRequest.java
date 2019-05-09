package feign.http;

import feign.Request;
import feign.RequestOptions;
import java.net.URI;

public final class HttpRequest implements Request {

  private URI uri;
  private HttpMethod method;
  private HttpHeader[] headers;
  private RequestOptions options;
  private byte[] content;

  public HttpRequest(URI uri, HttpMethod method, HttpHeader[] headers, RequestOptions options,
      byte[] content) {
    this.uri = uri;
    this.method = method;
    this.headers = headers;
    this.options = options;
    this.content = content;
  }

  public URI uri() {
    return this.uri;
  }

  public byte[] content() {
    return this.content;
  }

  public HttpMethod method() {
    return this.method;
  }

  public HttpHeader[] headers() {
    return this.headers;
  }

  public RequestOptions options() {
    return this.options;
  }

}
