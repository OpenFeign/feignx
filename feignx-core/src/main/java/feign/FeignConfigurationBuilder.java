package feign;

import feign.exception.ExceptionHandler;
import java.util.concurrent.Executor;

/**
 * Configuration Builder Definition for any Feign instance.
 *
 * @param <B> type of Builder to chain.
 * @param <C> type of FeignConfiguration to generate.
 */
public interface FeignConfigurationBuilder<B extends FeignConfigurationBuilder, C extends FeignConfiguration> {

  /**
   * Request Encoder to use.
   *
   * @param encoder instance.
   * @return the builder chain.
   */
  B encoder(RequestEncoder encoder);

  /**
   * Response Decoder to use.
   *
   * @param decoder instance.
   * @return the builder chain.
   */
  B decoder(ResponseDecoder decoder) ;

  /**
   * Request Interceptor to apply.
   *
   * @param interceptor instance.
   * @return the builder chain.
   */
  B interceptor(RequestInterceptor interceptor);

  /**
   * Client to use.
   *
   * @param client instance.
   * @return the builder chain.
   */
  B client(Client client);

  /**
   * Executor to use when executing requests.
   *
   * @param executor instance.
   * @return the builder chain.
   */
  B executor(Executor executor);

  /**
   * Contract to apply to the any Targets.
   *
   * @param contract to apply.
   * @return the builder chain.
   */
  B contract(Contract contract);

  /**
   * Target to build off.
   *
   * @param target instance.
   * @return the builder chain.
   */
  B target(Target<?> target);

  /**
   * Exception Handler to use.
   *
   * @param exceptionHandler instance.
   * @return the builder chain.
   */
  B exceptionHandler(ExceptionHandler exceptionHandler);

  /**
   * Build the Configuration.
   *
   * @return a FeignConfiguration instance.
   */
  C build();
}
