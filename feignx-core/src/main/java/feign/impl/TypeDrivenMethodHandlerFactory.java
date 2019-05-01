package feign.impl;

import feign.FeignConfiguration;
import feign.TargetMethodDefinition;
import feign.TargetMethodHandler;
import feign.TargetMethodHandlerFactory;

/**
 * Target Method Handler Factory that uses the {@link TargetMethodDefinition#getReturnType()}
 * to determine which Method Handler to create.
 */
public class TypeDrivenMethodHandlerFactory implements TargetMethodHandlerFactory {

  /**
   * Creates a new {@link TargetMethodHandler} based on the return type of the
   * {@link TargetMethodDefinition} provided.
   *
   * @param targetMethodDefinition to inspect.
   * @param configuration with the required dependencies.
   * @return a new {@link TargetMethodHandler} instance.
   */
  @Override
  public TargetMethodHandler create(TargetMethodDefinition targetMethodDefinition,
      FeignConfiguration configuration) {

    /* return a blocking handler */
    return new BlockingTargetMethodHandler(
        targetMethodDefinition,
        configuration.getRequestEncoder(),
        configuration.getRequestInterceptors(),
        configuration.getClient(),
        configuration.getResponseDecoder(),
        configuration.getExceptionHandler(),
        configuration.getExecutor());
  }
}
