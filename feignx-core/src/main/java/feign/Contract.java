package feign;

import java.util.Collection;

public interface Contract {

  Collection<TargetMethod> apply(Target<?> target);

}
