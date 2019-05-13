package feign;

import feign.http.HttpMethod;
import java.net.URI;
import java.util.List;

/**
 * Information about a Request to be made.
 */
public interface Request {

  /**
   * URI of the Target.
   *
   * @return request uri.
   */
  URI uri();

  /**
   * Content to be sent with the request.
   *
   * @return request content.
   */
  byte[] content();

  /**
   * Length of the Content to be sent.
   *
   * @return request content size.
   */
  int contentLength();

  /**
   * Http Method for this request.
   *
   * @return http method.
   */
  HttpMethod method();

  /**
   * Headers for the request.
   *
   * @return request headers.
   */
  List<Header> headers();

  /**
   * Options for this request.
   *
   * @return request options.
   */
  RequestOptions options();
}
