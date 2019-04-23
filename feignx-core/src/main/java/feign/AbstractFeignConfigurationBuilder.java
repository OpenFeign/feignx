package feign;

import feign.http.Client;

public abstract class AbstractFeignConfigurationBuilder
    <B extends AbstractFeignConfigurationBuilder<B, C>, C extends FeignConfiguration>
    implements FeignConfigurationBuilder<B, C> {

  protected final B self;
  protected Client client;

  protected AbstractFeignConfigurationBuilder(Class<B> self) {
    this.self = self.cast(this);
  }

  @Override
  public B client(Client client) {
    this.client = client;
    return this.self;
  }

  @Override
  public B encoder() {
    return this.self;
  }

  @Override
  public B decoder() {
    return this.self;
  }

}
