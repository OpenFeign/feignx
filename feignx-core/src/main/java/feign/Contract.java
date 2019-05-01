package feign;

import java.util.Collection;

public interface Contract {

  Collection<TargetMethodDefinition> apply(Target<?> target);

}
