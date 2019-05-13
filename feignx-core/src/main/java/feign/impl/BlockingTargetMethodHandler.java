package feign.impl;

import feign.Client;
import feign.Logger;
import feign.RequestEncoder;
import feign.RequestInterceptor;
import feign.Response;
import feign.ResponseDecoder;
import feign.TargetMethodDefinition;
import feign.ExceptionHandler;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.RunnableFuture;

/**
 * HttpMethod handler that uses the calling thread to process the request and response.
 */
public class BlockingTargetMethodHandler extends AbstractTargetMethodHandler {

  /**
   * Creates a new {@link BlockingTargetMethodHandler}.
   *
   * @param targetMethodDefinition containing the method configuration.
   * @param encoder to use when preparing the request.
   * @param interceptors to apply to the request before processing.
   * @param client to send the request and create the response.
   * @param decoder to use when parsing the response.
   * @param exceptionHandler to delegate to when an exception occurs.
   * @param logger to use.
   */
  BlockingTargetMethodHandler(TargetMethodDefinition targetMethodDefinition,
      RequestEncoder encoder, List<RequestInterceptor> interceptors,
      Client client, ResponseDecoder decoder,
      ExceptionHandler exceptionHandler, Executor executor, Logger logger) {
    /* create a new method handler, with a synchronous executor */
    super(targetMethodDefinition, encoder, interceptors, client, decoder, exceptionHandler,
        executor, logger);
  }

  /**
   * Blocks the calling thread, waiting for the result of the request.
   *
   * @param request being processed.
   * @return the decoded response.
   * @throws Exception if the response could not be processed.
   */
  @Override
  protected Object handleResponse(RunnableFuture<Response> request) throws Exception {

    /* pull the result of the task immediately, waiting for it to complete */
    log.debug("Waiting for the Response.");
    try (Response response = request.get()) {
      /* decode and close the response */
      log.debug("Response received, decoding. Response: {}", response);
      return this.decode(response);
    }
  }
}
