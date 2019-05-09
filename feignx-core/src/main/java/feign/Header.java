package feign;

import java.util.Collection;

/**
 * Represents a Header property for a Targeted Request.
 */
public interface Header {

  /**
   * Name of the Header.
   *
   * @return header name.
   */
  String name();

  /**
   * Values of the Header.
   *
   * @return header values.
   */
  Collection<String> values();

  /**
   * Add a Value to this Header.
   *
   * @param value to add.
   */
  void value(String value);
}
