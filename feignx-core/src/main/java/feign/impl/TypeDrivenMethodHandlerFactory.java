package feign.impl;

import feign.Target;
import feign.TargetMethodHandler;
import feign.TargetMethodHandlerFactory;

public class TypeDrivenMethodHandlerFactory implements TargetMethodHandlerFactory {

  @Override
  public <T> TargetMethodHandler create(Target<T> target,
      TargetMethodMetadata targetMethodMetadata) {
    return null;
  }
}
