package feign.impl.type;

import feign.support.Assert;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ParameterizedTypeWrapper implements TypeWrapper {

  private final ParameterizedType type;

  public ParameterizedTypeWrapper(ParameterizedType type) {
    Assert.isNotNull(type, "type is required.");
    this.type = type;
  }

  @Override
  public Class<?> getType() {
    Type rawType = type.getRawType();
    if (rawType instanceof Class) {
      return (Class) rawType;
    }
    return null;
  }
}
