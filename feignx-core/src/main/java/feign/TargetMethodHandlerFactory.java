package feign;

/**
 * Factory that creates TargetMethodHandler instances.
 */
public interface TargetMethodHandlerFactory {

  /**
   * Creates a new TargetMethodHandler, using the definition and configuration provided.
   *
   * @param targetMethodDefinition for the method to handle.
   * @param feignConfiguration with the shared configuration.
   * @return a TargetMethodHandler instance.
   */
  TargetMethodHandler create(TargetMethodDefinition targetMethodDefinition,
      FeignConfiguration feignConfiguration);

}
