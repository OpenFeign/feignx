package feign.http;

import feign.http.RequestSpecification;

@FunctionalInterface
public interface RequestEncoder {

  void encode(RequestSpecification requestSpecification);

}
