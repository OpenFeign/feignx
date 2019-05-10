package feign;

import feign.contract.FeignContract;
import feign.decoder.StringDecoder;
import feign.encoder.StringEncoder;
import feign.exception.ExceptionHandler.RethrowExceptionHandler;
import feign.http.client.UrlConnectionClient;
import feign.impl.AbstractFeignConfigurationBuilder;
import feign.impl.BaseFeignConfiguration;
import feign.impl.UriTarget;
import feign.proxy.ProxyFeign;

public abstract class Feign {

  public static FeignConfigurationBuilderImpl builder() {
    return new FeignConfigurationBuilderImpl();
  }

  protected abstract <T> T create(FeignConfiguration configuration);

  static class FeignConfigurationBuilderImpl extends
      AbstractFeignConfigurationBuilder<FeignConfigurationBuilderImpl, FeignConfiguration> {

    FeignConfigurationBuilderImpl() {
      super(FeignConfigurationBuilderImpl.class);

      /* set our defaults */
      this.contract = new FeignContract();
      this.encoder = new StringEncoder();
      this.decoder = new StringDecoder();
      this.client = new UrlConnectionClient();
      this.exceptionHandler = new RethrowExceptionHandler();
      this.executor = Runnable::run;
    }

    @Override
    public FeignConfiguration build() {
      return new BaseFeignConfiguration(
          this.target, this.contract, this.encoder, this.interceptors, this.client, this.decoder,
          this.exceptionHandler, this.executor);
    }

    public <T> T target(Class<T> targetType, String uri) {
      this.target(new UriTarget<>(targetType, uri));

      /* create a new ProxyFeign instance */
      Feign feign = new ProxyFeign();
      return feign.create(this.build());
    }
  }


}
