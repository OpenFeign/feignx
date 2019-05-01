package feign;

import feign.exception.ExceptionHandler;
import java.util.List;
import java.util.concurrent.Executor;

public interface FeignConfiguration {

  Client getClient();

  RequestEncoder getRequestEncoder();

  ResponseDecoder getResponseDecoder();

  Contract getContract();

  Executor getExecutor();

  List<RequestInterceptor> getRequestInterceptors();

  ExceptionHandler getExceptionHandler();

  <T> Target<T> getTarget();
}
