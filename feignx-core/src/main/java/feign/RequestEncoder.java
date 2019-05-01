package feign;

import feign.http.RequestSpecification;

@FunctionalInterface
public interface RequestEncoder {

  void apply(Object content, RequestSpecification requestSpecification);

}
