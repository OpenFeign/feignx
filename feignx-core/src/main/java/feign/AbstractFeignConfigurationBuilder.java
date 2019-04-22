package feign;

public abstract class AbstractFeignConfigurationBuilder<S extends AbstractFeignConfigurationBuilder<S>>
    implements FeignConfiguration<S> {

  protected final S self;

  protected AbstractFeignConfigurationBuilder(Class<S> self) {
    this.self = self.cast(this);
  }

  @Override
  public S client() {
    return null;
  }

  @Override
  public S encoder() {
    return null;
  }

  @Override
  public S decoder() {
    return null;
  }

  public abstract <T> T target(Class<T> type, String uri);
}
