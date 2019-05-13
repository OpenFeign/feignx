package feign.impl.type;

import java.lang.reflect.Type;

/**
 * Type Definition for a concrete Class.
 */
public class ClassTypeDefinition implements TypeDefinition, Type {

  private final Class<?> type;

  /**
   * Creates a new ClassTypeDefinition.
   *
   * @param type to wrap.
   */
  ClassTypeDefinition(Class<?> type) {
    this.type = type;
  }

  @Override
  public Class<?> getType() {
    return this.type;
  }
}
