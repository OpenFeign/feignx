package feign.contract;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for a Method Parameter value that maps to a URI template expression within the
 * path segment.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathParam {

  /**
   * Name of the Path Parameter.
   *
   * @return parameter name.
   */
  String value();

}
