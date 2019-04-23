package feign;

public abstract class Feign {

  public static FeignConfigurationBuilder builder() {
    return new FeignConfigurationBuilder();
  }

  protected abstract <T> T create(FeignConfiguration configuration);

  static class FeignConfigurationBuilder extends
      AbstractFeignConfigurationBuilder<FeignConfigurationBuilder, FeignConfiguration> {

    public FeignConfigurationBuilder() {
      super(FeignConfigurationBuilder.class);
    }

    @Override
    public FeignConfiguration build() {
      return null;
    }

    <T> T target(Class<T> target, String uri) {
      return null;
    }
  }



}
