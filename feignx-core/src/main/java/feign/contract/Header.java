package feign.contract;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(Headers.class)
public @interface Header {

  /**
   * Name of the Header.  Examples are {@literal Content-Type} and {@literal Authorization}
   * @return header name.
   */
  String name();

  /**
   * Value of the Header.  Must be a literal.
   *
   * @return header value.
   */
  String value();


  Param parameter();
}
