package feign;

public interface TargetMethodHandlerFactory {

  TargetMethodHandler create(TargetMethodDefinition targetMethodDefinition,
      FeignConfiguration feignConfiguration);

}
