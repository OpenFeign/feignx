package feign;

import feign.impl.TargetMethodMetadata;

public interface TargetMethodHandlerFactory {

  <T> TargetMethodHandler create(Target<T> target, TargetMethodMetadata targetMethodMetadata);

}
