package feign;

import feign.proxy.ProxyFeign;
import feign.target.UrlTarget;

public abstract class Feign {

  public static FeignConfigurationBuilderImpl builder() {
    return new FeignConfigurationBuilderImpl();
  }

  protected abstract <T> T create(FeignConfiguration configuration);

  static class FeignConfigurationBuilderImpl extends
      AbstractFeignConfigurationBuilder<FeignConfigurationBuilderImpl, FeignConfiguration> {

    FeignConfigurationBuilderImpl() {
      super(FeignConfigurationBuilderImpl.class);
    }

    @Override
    public FeignConfiguration build() {
      return new BaseFeignConfiguration(
          this.client, null, null, null, this.target);
    }

    public <T> T target(Class<T> targetType, String uri) {
      this.target(new UrlTarget<>(targetType, uri));

      /* create a new ProxyFeign instance */
      Feign feign = new ProxyFeign();
      return feign.create(this.build());
    }
  }


}
