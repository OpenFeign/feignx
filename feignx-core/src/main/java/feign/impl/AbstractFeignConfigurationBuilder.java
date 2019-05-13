package feign.impl;

import feign.Client;
import feign.Contract;
import feign.FeignConfiguration;
import feign.FeignConfigurationBuilder;
import feign.RequestEncoder;
import feign.RequestInterceptor;
import feign.ResponseDecoder;
import feign.Target;
import feign.exception.ExceptionHandler;
import feign.support.Assert;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Base Configuration Builder.  Can be extended to add additional fluent methods customizing
 * specific Feign extensions.
 *
 * @param <B> {@link FeignConfigurationBuilder} to chain.
 * @param <C> {@link FeignConfiguration} being built.
 */
public abstract class AbstractFeignConfigurationBuilder
    <B extends AbstractFeignConfigurationBuilder<B, C>, C extends FeignConfiguration>
    implements FeignConfigurationBuilder<B, C> {

  protected final B self;
  protected Client client;
  protected RequestEncoder encoder;
  protected List<RequestInterceptor> interceptors = new ArrayList<>();
  protected ResponseDecoder decoder;
  protected Executor executor;
  protected Contract contract;
  protected ExceptionHandler exceptionHandler;
  protected Target<?> target;

  /**
   * Creates a new FeignConfigurationBuilder.
   *
   * @param self reference to the builder implementation to chain.
   */
  protected AbstractFeignConfigurationBuilder(Class<B> self) {
    this.self = self.cast(this);
  }

  /**
   * Client implementation to use.
   *
   * @param client instance.
   * @return the builder reference chain.
   */
  @Override
  public B client(Client client) {
    Assert.isNotNull(client, "client cannot be null.");
    this.client = client;
    return this.self;
  }

  /**
   * Request Encoder implementation to use.
   *
   * @param encoder instance.
   * @return the builder reference chain.
   */
  @Override
  public B encoder(RequestEncoder encoder) {
    Assert.isNotNull(encoder, "encoder cannot be null");
    this.encoder = encoder;
    return this.self;
  }

  /**
   * Response Decoder implementation to use.
   *
   * @param decoder instance.
   * @return the builder reference chain.
   */
  @Override
  public B decoder(ResponseDecoder decoder) {
    Assert.isNotNull(decoder, "decoder cannot be null.");
    this.decoder = decoder;
    return this.self;
  }

  /**
   * Executor to use when {@link Client}s are submitting requests.
   *
   * @param executor instance.
   * @return the builder reference chain.
   */
  @Override
  public B executor(Executor executor) {
    Assert.isNotNull(executor, "executor cannot be null.");
    this.executor = executor;
    return this.self;
  }

  /**
   * Contract to apply to the Target instances.
   *
   * @param contract to apply.
   * @return the builder reference chain.
   */
  @Override
  public B contract(Contract contract) {
    Assert.isNotNull(contract, "contract cannot be null.");
    this.contract = contract;
    return this.self;
  }

  /**
   * Target instance to wrap.
   *
   * @param target instance.
   * @return the builder reference chain.
   */
  @Override
  public B target(Target<?> target) {
    Assert.isNotNull(target, "target cannot be null.");
    this.target = target;
    return this.self;
  }

  /**
   * Exception Handler implementation to use.
   *
   * @param exceptionHandler instance.
   * @return the builder reference chain.
   */
  @Override
  public B exceptionHandler(ExceptionHandler exceptionHandler) {
    Assert.isNotNull(exceptionHandler, "exception handler cannot be null.");
    this.exceptionHandler = exceptionHandler;
    return this.self;
  }

  /**
   * Request Interceptor to apply.
   *
   * @param interceptor instance.
   * @return the builder reference chain.
   */
  @Override
  public B interceptor(RequestInterceptor interceptor) {
    Assert.isNotNull(interceptor, "interceptor cannot be null.");
    this.interceptors.add(interceptor);
    return this.self;
  }
}
