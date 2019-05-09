package feign.impl;

import feign.Client;
import feign.Request;
import feign.RequestEncoder;
import feign.RequestInterceptor;
import feign.Response;
import feign.ResponseDecoder;
import feign.TargetMethodDefinition;
import feign.TargetMethodHandler;
import feign.exception.ExceptionHandler;
import feign.exception.FeignException;
import feign.http.RequestSpecification;
import feign.support.Assert;
import feign.template.TemplateParameter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

/**
 * Base HttpMethod Handler implementation.  HttpRequest preparation is always done on the calling
 * thread.  HttpRequest execution is delegated to the provided {@link Executor}.  Response processing is
 * the responsibility of the subclasses.
 */
public abstract class AbstractTargetMethodHandler implements TargetMethodHandler {

  private TargetMethodDefinition targetMethodDefinition;
  private RequestEncoder encoder;
  private List<RequestInterceptor> interceptors;
  private Client client;
  private ResponseDecoder decoder;
  private ExceptionHandler exceptionHandler;
  private Executor executor;

  /**
   * Creates a new Abstract Target HttpMethod Handler.
   *
   * @param targetMethodDefinition containing the method configuration.
   * @param encoder to use when preparing the request.
   * @param interceptors to apply to the request before processing.
   * @param client to send the request and create the response.
   * @param decoder to use when parsing the response.
   * @param exceptionHandler to delegate to when an exception occurs.
   * @param executor to execute the request on.
   */
  protected AbstractTargetMethodHandler(
      TargetMethodDefinition targetMethodDefinition, RequestEncoder encoder,
      List<RequestInterceptor> interceptors, Client client, ResponseDecoder decoder,
      ExceptionHandler exceptionHandler, Executor executor) {
    Assert.isNotNull(targetMethodDefinition, "targetMethodDefinition is required.");
    Assert.isNotNull(encoder, "encoder is required.");
    Assert.isNotNull(client, "client is required.");
    Assert.isNotNull(decoder, "decoder is required.");
    Assert.isNotNull(exceptionHandler, "exceptionHandler is required.");
    Assert.isNotNull(executor, "executor is required.");
    this.encoder = encoder;
    this.interceptors = (interceptors == null) ? Collections.emptyList() : interceptors;
    this.client = client;
    this.decoder = decoder;
    this.exceptionHandler = exceptionHandler;
    this.executor = executor;
    this.targetMethodDefinition = targetMethodDefinition;
  }

  /**
   * Execute the HttpRequest pipeline.
   *
   * @param arguments for the method.
   * @return the result of the request.
   */
  @Override
  public Object execute(Object[] arguments) {

    try {
      /* prepare the request specification */
      final RequestSpecification requestSpecification = this.targetMethodDefinition
          .requestSpecification(this.mapArguments(arguments));

      /* apply any interceptors */
      for (RequestInterceptor interceptor : this.interceptors) {
        interceptor.accept(requestSpecification);
      }

      /* encode the request */
      if (this.targetMethodDefinition.getBody() != -1) {
        this.encoder.apply(arguments[this.targetMethodDefinition.getBody()], requestSpecification);
      }

      /* execute the request on the provided executor */
      RunnableFuture<Response> task = this.getTask(targetMethodDefinition, requestSpecification.build());
      this.executor.execute(task);

      /* process the results of the task */
      return this.handleResponse(task);

    } catch (Exception ex) {
      this.exceptionHandler.accept(
          new FeignException(
              "Error occurred during method processing", ex, targetMethodDefinition.getTag()));
    }

    /* method was not handled properly */
    throw methodNotHandled();
  }

  /**
   * Process the results of the HttpRequest.
   *
   * @param response Future containing the results of the request.
   * @return the result of the request, decoded if necessary.
   * @throws Exception in the event the response could not be processed.
   */
  protected abstract Object handleResponse(RunnableFuture<Response> response) throws Exception;

  /**
   * Decode the Response.
   *
   * @param response to decode.
   * @return the desired decoded result
   * @throws feign.exception.FeignException if the response could not be decoded.
   */
  protected Object decode(Response response) {
    Class<?> returnType = this.targetMethodDefinition.getReturnType();
    if (Response.class == returnType) {
      /* no need to decode */
      return response;
    } else {
      /* decode the response */
      return this.decoder.decode(response, returnType);
    }
  }

  /**
   * Map the Argument list to any registered Template Parameters.
   *
   * @param arguments to map.
   * @return a new Map, where the argument is matched up to the corresponding Template parameter
   * name.
   */
  private Map<String, Object> mapArguments(Object[] arguments) {
    Map<String, Object> variables = new LinkedHashMap<>();
    for (int i = 0; i < arguments.length; i++) {
      final Object argument = arguments[i];
      Optional<TemplateParameter> templateParameter =
          this.targetMethodDefinition.getTemplateParameter(i);
      templateParameter.ifPresent(parameter -> variables.put(parameter.name(), argument));
    }
    return variables;
  }

  /**
   * Creates a new {@link RunnableFuture} wrapping the {@link Client}.
   *
   * @param request to send.
   * @return a Future containing the result of the request.
   * @throws IllegalStateException if the task could not be handled properly.
   */
  private RunnableFuture<Response> getTask(
      final TargetMethodDefinition methodMetadata, final Request request) {
    return new FutureTask<>(() -> {
      try {
        return client.request(request);
      } catch (Exception ex) {
        exceptionHandler.accept(
            new FeignException(
                "Error occurred during request processing", ex, methodMetadata.getTag()));
      }
      throw methodNotHandled();
    });
  }

  /**
   * Creates a new Illegal State Exception in the event that either the task or the method
   * invocation was not handled correctly.
   *
   * @return a RuntimeException describing the illegal state.
   */
  private RuntimeException methodNotHandled() {
    return new IllegalStateException("Error occurred when trying to execute the request "
        + "and either no Response was returned or an Exception was left unhandled.");
  }

}
