package feign.proxy;

import feign.Feign;
import feign.FeignConfiguration;
import feign.TargetMethodDefinition;
import java.lang.reflect.Proxy;
import java.util.Collection;

/**
 * Feign implementation that creates a JDK Proxy for the {@link feign.Target} instance.
 */
public class ProxyFeign extends Feign {

  /**
   * Creates a JDK Proxy for the {@link feign.Target} provided in the configuration.
   *
   * @param configuration for this instance.
   * @param <T> of the {@link feign.Target} class to proxy.
   * @return a JDK Proxy instance.
   */
  @SuppressWarnings("unchecked")
  @Override
  protected <T> T create(FeignConfiguration configuration) {
    /* apply the contract to the target */
    Collection<TargetMethodDefinition> targetMethodMetadata =
        configuration.getContract().apply(configuration.getTarget());

    /* create the provided target in a proxy */
    ProxyTarget<T> proxyTarget = new ProxyTarget<>(targetMethodMetadata, configuration);

    /* create a new JDK Proxy for the Target */
    return (T) Proxy.newProxyInstance(
        proxyTarget.type().getClassLoader(),
        new Class<?>[] {proxyTarget.type()},
        proxyTarget);
  }
}
