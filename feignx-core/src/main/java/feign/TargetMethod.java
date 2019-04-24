package feign;

import feign.http.RequestSpecification;

public interface TargetMethod {

  String name();

  RequestSpecification requestSpecification();

}
