package feign;

import feign.http.Client;

public interface FeignConfigurationBuilder<B extends FeignConfigurationBuilder, C extends FeignConfiguration> {

  B encoder();

  B decoder() ;

  B client(Client client);

  B target(Target<?> target);

  C build();
}
