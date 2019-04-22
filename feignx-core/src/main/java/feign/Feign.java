package feign;

public abstract class Feign {

  public static FeignConfigurationBuilder builder() {
    return new FeignConfigurationBuilder();
  }

  protected abstract <T> T create(Target<T> target);

  static class FeignConfigurationBuilder extends
      AbstractFeignConfigurationBuilder<FeignConfigurationBuilder> {

    public FeignConfigurationBuilder() {
      super(FeignConfigurationBuilder.class);
    }

    @Override
    public <T> T target(Class<T> type, String uri) {
      return null;
    }
  }
}
