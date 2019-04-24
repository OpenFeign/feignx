package feign.impl;

import feign.TargetMethodHandler;
import feign.exception.ExceptionHandler;
import feign.http.Client;
import feign.http.Request;
import feign.http.RequestEncoder;
import feign.http.RequestInterceptor;
import feign.http.RequestSpecification;
import feign.http.Response;
import feign.http.ResponseDecoder;
import feign.support.Assert;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

public abstract class AbstractTargetMethodHandler implements TargetMethodHandler {

  private TargetMethodMetadata targetMethodMetadata;
  private RequestEncoder encoder;
  private List<RequestInterceptor> interceptors;
  private Client client;
  private ResponseDecoder decoder;
  private ExceptionHandler exceptionHandler;
  private Executor executor;

  protected AbstractTargetMethodHandler(
      TargetMethodMetadata targetMethodMetadata, RequestEncoder encoder,
      List<RequestInterceptor> interceptors, Client client, ResponseDecoder decoder,
      ExceptionHandler exceptionHandler, Executor executor) {
    Assert.isNotNull(targetMethodMetadata, "targetMethodMetadata is required.");
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
    this.targetMethodMetadata = targetMethodMetadata;
  }

  /**
   * Execute the Request pipeline.
   *
   * @param args for the method.
   * @return the result of the request.
   * @throws Throwable if an error occurs.
   */
  @Override
  public Object execute(Object[] args) throws Throwable {

    final Class<?> returnType = this.targetMethodMetadata.getReturnType().getClass();

    try {
      /* prepare the request specification */
      final RequestSpecification requestSpecification = this.targetMethodMetadata
          .requestSpecification();

      /* apply the arguments to the specification */
      requestSpecification.apply(args);

      /* apply any interceptors */
      for (RequestInterceptor interceptor : this.interceptors) {
        interceptor.accept(requestSpecification);
      }

      /* encode the request */
      this.encoder.accept(requestSpecification);

      /* execute the request on the provided executor */
      RunnableFuture<Response> task = this.getTask(requestSpecification.build());
      this.executor.execute(task);

      /* process the results of the task */
      return this.handleTask(task);

    } catch (Exception ex) {
      this.exceptionHandler.accept(ex);
    }

    /* method was not handled properly */
    throw methodNotHandled();
  }

  protected abstract Object handleTask(RunnableFuture<Response> task);

  protected Object decode(Response response) {
    Class<?> returnType = this.targetMethodMetadata.getReturnType().getClass();
    if (Response.class.isAssignableFrom(returnType)) {
      /* no need to decode */
      return response;
    } else {
      /* decode the response */
      return this.decoder.decode(response, returnType);
    }
  }

  /**
   * Creates a new {@link RunnableFuture} wrapping the {@link Client}.
   *
   * @param request to send.
   * @return a Future containing the result of the request.
   */
  private RunnableFuture<Response> getTask(final Request request) {
    return new FutureTask<>(() -> {
      try {
        return client.request(request);
      } catch (Exception ex) {
        exceptionHandler.accept(ex);
      }
      throw methodNotHandled();
    });
  }

  /**
   * Creates a new Illegal State Exception in the event that either the task or the
   * method invocation was not handled correctly.
   *
   * @return a RuntimeException describing the illegal state.
   */
  protected RuntimeException methodNotHandled() {
    return new IllegalStateException("Error occurred when trying to execute the request "
        + "and either no Response was returned or an Exception was left unhandled.");
  }

  protected ResponseDecoder getDecoder() {
    return decoder;
  }

  protected ExceptionHandler getExceptionHandler() {
    return exceptionHandler;
  }
}
