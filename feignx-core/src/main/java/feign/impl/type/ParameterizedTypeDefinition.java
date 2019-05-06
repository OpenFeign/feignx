package feign.impl.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ParameterizedTypeDefinition implements ParameterizedType, TypeDefinition {

  private final Type owner;
  private final Type raw;
  private final Type[] arguments;

  ParameterizedTypeDefinition(Type owner, Type raw, Type... arguments) {
    this.owner = owner;
    this.raw = raw;
    this.arguments = arguments.clone();
  }

  @Override
  public Class<?> getType() {
    return (Class<?>) this.getRawType();
  }

  @Override
  public Type[] getActualTypeArguments() {
    return arguments.clone();
  }

  @Override
  public Type getRawType() {
    return this.raw;
  }

  @Override
  public Type getOwnerType() {
    return this.owner;
  }
}
