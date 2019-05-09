package feign;

import java.util.Collection;

/**
 * Represents the agreement between the specific implementation and the user, enforcing how
 * each Target method can be defined.
 */
public interface Contract {

  Collection<TargetMethodDefinition> apply(Target<?> target);

}
