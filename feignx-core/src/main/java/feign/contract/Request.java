package feign.contract;

import feign.http.HttpMethod;
import feign.RequestOptions;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that provides the HTTP HttpMethod, URI template, and HttpHeader to apply to this
 * request.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Request {

  /**
   * Uri for this HttpRequest.
   *
   * @return the uri.
   */
  String value();

  /**
   * HTTP HttpMethod for this request.  Defaults to GET.
   *
   * @return http method.
   */
  HttpMethod method() default HttpMethod.GET;

  /**
   * Uri for the HttpRequest.  Alias for value.
   *
   * @return the uri.
   */
  String uri() default "";

  /**
   * Flag that determines if this request should follow any 3xx response codes automatically.
   * Default is {@literal true}
   *
   * @return if this request should follow redirect responses.
   */
  boolean followRedirects() default true;

  /**
   * HttpRequest Connection Timeout value, in milliseconds.
   *
   * @return how long to wait before failing when connecting to the target.
   */
  long connectTimeout() default RequestOptions.DEFAULT_CONNECT_TIMEOUT;

  /**
   * Read Timeout value, in milliseconds.
   *
   * @return how long to wait before failing when reading data from the target.
   */
  long readTimeout() default RequestOptions.DEFAULT_READ_TIMEOUT;

}
