package feign.contract;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for a Method Parameter value that maps to a URI template expression within the
 * query segment.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryParam {

  /**
   * Name of the Query Parameter.
   *
   * @return parameter name.
   */
  String value();

  /**
   * Flag to indicate if this value should be pct-encoded.
   *
   * @return if this parameter should be encoded.
   */
  boolean encode() default true;

}
