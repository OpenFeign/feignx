package feign.impl.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeFactory {

  public static TypeWrapper wrap(Type type) {
    if (type instanceof Class<?>) {
      return new ClassTypeWrapper((Class) type);
    } else if (type instanceof ParameterizedType) {
      return new ParameterizedTypeWrapper((ParameterizedType) type);
    }
    return null;
  }
}
