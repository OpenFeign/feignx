package feign;

import feign.http.RequestSpecification;

public interface Target<T> {

  Class<T> type();

  String name();

  void apply(RequestSpecification requestSpecification);
}
