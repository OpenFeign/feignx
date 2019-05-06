package feign.impl.type;

import java.lang.reflect.Type;

public class ClassTypeDefinition implements TypeDefinition, Type {

  private final Class<?> type;

  ClassTypeDefinition(Class<?> type) {
    this.type = type;
  }

  @Override
  public Class<?> getType() {
    return this.type;
  }
}
