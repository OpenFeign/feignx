package feign;

import java.util.Collection;

public interface Target<T> {

  Class<T> type();

  String name();

  Collection<TargetMethod> methods();

  TargetMethod method(String name);

  void method(String name, TargetMethod method);

}
