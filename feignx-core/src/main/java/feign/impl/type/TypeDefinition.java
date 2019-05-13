package feign.impl.type;

import java.lang.reflect.Type;

/**
 * Generic Type Definition.
 */
public interface TypeDefinition extends Type {

  /**
   * The Class type defining this Type.
   *
   * @return class reference.
   */
  Class<?> getType();

}
