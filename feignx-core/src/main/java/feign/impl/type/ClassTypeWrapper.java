package feign.impl.type;

import feign.support.Assert;

public class ClassTypeWrapper implements TypeWrapper {

  private final Class<?> type;

  public ClassTypeWrapper(Class<?> type) {
    Assert.isNotNull(type, "type is required.");
    this.type = type;
  }


  @Override
  public Class<?> getType() {
    return type;
  }
}
