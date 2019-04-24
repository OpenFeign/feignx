package feign;

import feign.impl.TargetMethodMetadata;
import java.util.Collection;

public interface Contract {

  Collection<TargetMethodMetadata> apply(Target<?> target);

}
