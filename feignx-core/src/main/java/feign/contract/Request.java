package feign.contract;

import feign.http.Method;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that provides the HTTP Method, URI template, and Headers to apply to this
 * request.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Request {

  /**
   * Uri for this Request.
   *
   * @return the uri.
   */
  String value();

  /**
   * HTTP Method for this request.  Defaults to GET.
   *
   * @return http method.
   */
  Method method() default Method.GET;

  /**
   * Uri for the Request.  Alias for value.
   *
   * @return the uri.
   */
  String uri() default "";

  /**
   * Headers for this Request.
   *
   * @return an array of {@link Header}s
   */
  Header[] headers() default {};
}
