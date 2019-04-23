package feign.target;

import feign.Target;
import feign.TargetMethod;
import feign.support.Assert;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractTarget<T> implements Target<T> {

  private final Class<T> type;
  private final String name;
  private final Map<String, TargetMethod> methodMap = new LinkedHashMap<>();

  protected AbstractTarget(Class<T> type) {
    Assert.isNotNull(type, "type is required.");
    this.type = type;
    this.name = type.getSimpleName();
  }

  protected AbstractTarget(Class<T> type, String name) {
    Assert.isNotNull(type, "type is required.");
    Assert.isNotEmpty(name, "name is required.");
    this.type = type;
    this.name = name;
  }

  @Override
  public Class<T> type() {
    return this.type;
  }

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public Collection<TargetMethod> methods() {
    return Collections.unmodifiableCollection(this.methodMap.values());
  }

  @Override
  public TargetMethod method(String name) {
    return this.methodMap.get(name);
  }

  @Override
  public void method(String name, TargetMethod method) {
    this.methodMap.put(name, method);
  }
}
