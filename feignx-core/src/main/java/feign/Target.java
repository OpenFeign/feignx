package feign;

import feign.http.RequestSpecification;

/**
 * Represents a Target interface, containing the service definition.
 *
 * @param <T> type of the Target.
 */
public interface Target<T> {

  /**
   * Interface Type for this Target.
   *
   * @return the service definition type.
   */
  Class<T> type();

  /**
   * Short descriptive name for this Target.
   *
   * @return target name.
   */
  String name();

  /**
   * "Target"s the specification.
   *
   * @param requestSpecification to target.
   */
  void apply(RequestSpecification requestSpecification);
}
