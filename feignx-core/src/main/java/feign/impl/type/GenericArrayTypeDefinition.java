package feign.impl.type;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

public class GenericArrayTypeDefinition implements GenericArrayType, TypeDefinition {

  private final TypeDefinition genericComponentType;

  GenericArrayTypeDefinition(TypeDefinition genericComponentType) {
    this.genericComponentType = genericComponentType;
  }

  @Override
  public Class<?> getType() {
    return this.genericComponentType.getType();
  }

  @Override
  public Type getGenericComponentType() {
    return this.genericComponentType;
  }
}
