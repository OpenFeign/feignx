package feign;

import feign.http.RequestSpecification;

/**
 * Consumer responsible for encoding the provided object for use on the Request.
 */
@FunctionalInterface
public interface RequestEncoder {

  void apply(Object content, RequestSpecification requestSpecification);

}
