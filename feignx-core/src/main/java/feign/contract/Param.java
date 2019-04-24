package feign.contract;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that represents a template parameter.
 */
@Target({ElementType.PARAMETER, ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {

  /**
   * Name of the Parameter.
   *
   * @return parameter name.
   */
  String value();

  /**
   * Determines if the resolved value of this parameter should be pct-encoded.  Default is
   * {@literal true}
   *
   * @return if this parameter value should be pct-encoded.
   */
  boolean encode() default true;
}
