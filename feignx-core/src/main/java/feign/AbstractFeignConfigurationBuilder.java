package feign;

import feign.http.Client;
import feign.support.Assert;

public abstract class AbstractFeignConfigurationBuilder
    <B extends AbstractFeignConfigurationBuilder<B, C>, C extends FeignConfiguration>
    implements FeignConfigurationBuilder<B, C> {

  protected final B self;
  protected Client client;
  protected Target<?> target;

  protected AbstractFeignConfigurationBuilder(Class<B> self) {
    this.self = self.cast(this);
  }

  @Override
  public B client(Client client) {
    Assert.isNotNull(client, "client cannot be null.");
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

  @Override
  public B target(Target<?> target) {
    Assert.isNotNull(target, "target cannot be null.");
    this.target = target;
    return this.self;
  }
}
