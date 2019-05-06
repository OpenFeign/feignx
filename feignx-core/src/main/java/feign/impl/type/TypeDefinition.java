package feign.impl.type;

import java.lang.reflect.Type;

public interface TypeDefinition extends Type {

  Class<?> getType();

}
