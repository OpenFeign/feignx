package feign.impl.type;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

/**
 * Type Definition for a {@link GenericArrayType}, a ParameterizedType with an Array supplied
 * as the Type Variable.
 */
public class GenericArrayTypeDefinition implements GenericArrayType, TypeDefinition {

  private final TypeDefinition genericComponentType;

  /**
   * Creates a new GenericArrayTypeDefiniton.
   *
   * @param genericComponentType containing the Type to define.
   */
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
