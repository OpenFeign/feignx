package feign;

import feign.exception.ExceptionHandler;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Configuration Definition.
 */
public interface FeignConfiguration {

  /**
   * Client instance to use to execute requests.
   *
   * @return a client instance.
   */
  Client getClient();

  /**
   * Request Encoder to apply to requests.
   *
   * @return a request encoder instance.
   */
  RequestEncoder getRequestEncoder();

  /**
   * Response Decoder to apply to responses.
   *
   * @return a response decoder instance.
   */
  ResponseDecoder getResponseDecoder();

  /**
   * Contract to apply to Targets.
   *
   * @return a contract instance.
   */
  Contract getContract();

  /**
   * Executor to use when executing requests on a Client.
   *
   * @return an executor instance.
   */
  Executor getExecutor();

  /**
   * Interceptors to apply to requests.
   *
   * @return a list of interceptor instances.
   */
  List<RequestInterceptor> getRequestInterceptors();

  /**
   * Exception Handler to apply in case of an exception.
   *
   * @return an exception handler instance.
   */
  ExceptionHandler getExceptionHandler();

  /**
   * Target instance for this configuration.
   *
   * @param <T> type of the Target.
   * @return the Target instance.
   */
  <T> Target<T> getTarget();
}
