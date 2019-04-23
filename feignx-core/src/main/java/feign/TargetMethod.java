package feign;

import feign.http.RequestSpecification;

public interface TargetMethod {

  RequestSpecification requestSpecification();

  <R> R execute(Object[] args);

}
