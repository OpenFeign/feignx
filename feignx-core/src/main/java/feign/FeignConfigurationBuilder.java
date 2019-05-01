package feign;

import feign.exception.ExceptionHandler;
import java.util.concurrent.Executor;

public interface FeignConfigurationBuilder<B extends FeignConfigurationBuilder, C extends FeignConfiguration> {

  B encoder(RequestEncoder encoder);

  B decoder(ResponseDecoder decoder) ;

  B interceptor(RequestInterceptor interceptor);

  B client(Client client);

  B executor(Executor executor);

  B contract(Contract contract);

  B target(Target<?> target);

  B exceptionHandler(ExceptionHandler exceptionHandler);

  C build();
}
