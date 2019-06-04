/*
 * Copyright 2019 OpenFeign Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package feign.impl;

import feign.Client;
import feign.ExceptionHandler;
import feign.Logger;
import feign.Request;
import feign.RequestEncoder;
import feign.RequestInterceptor;
import feign.Response;
import feign.ResponseDecoder;
import feign.Retry;
import feign.TargetMethodDefinition;
import feign.TargetMethodHandler;
import feign.exception.FeignException;
import feign.http.RequestSpecification;
import feign.impl.type.TypeDefinition;
import feign.support.Assert;
import feign.template.TemplateParameter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.slf4j.LoggerFactory;

/**
 * Base HttpMethod Handler implementation.  HttpRequest preparation is always done on the calling
 * thread.  HttpRequest execution is delegated to the provided {@link Executor}.  Response
 * processing is the responsibility of the subclasses.
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbstractTargetMethodHandler implements TargetMethodHandler {

  protected final org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
  protected TargetMethodDefinition targetMethodDefinition;
  private RequestEncoder encoder;
  private List<RequestInterceptor> interceptors;
  private Client client;
  private ResponseDecoder decoder;
  private ExceptionHandler exceptionHandler;
  private Logger logger;
  private Executor executor;
  private Retry retry;

  /**
   * Creates a new Abstract Target HttpMethod Handler.
   *
   * @param targetMethodDefinition containing the method configuration.
   * @param encoder to use when preparing the request.
   * @param interceptors to apply to the request before processing.
   * @param client to send the request and create the response.
   * @param decoder to use when parsing the response.
   * @param exceptionHandler to delegate to when an exception occurs.
   * @param executor to request the request on.
   * @param logger for logging requests and responses.
   * @param retry for retrying requests.
   */
  protected AbstractTargetMethodHandler(
      TargetMethodDefinition targetMethodDefinition, RequestEncoder encoder,
      List<RequestInterceptor> interceptors, Client client, ResponseDecoder decoder,
      ExceptionHandler exceptionHandler, Executor executor, Logger logger,
      Retry retry) {
    Assert.isNotNull(targetMethodDefinition, "targetMethodDefinition is required.");
    Assert.isNotNull(encoder, "encoder is required.");
    Assert.isNotNull(client, "client is required.");
    Assert.isNotNull(decoder, "decoder is required.");
    Assert.isNotNull(exceptionHandler, "exceptionHandler is required.");
    Assert.isNotNull(executor, "executor is required.");
    Assert.isNotNull(retry, "retry is required");
    Assert.isNotNull(logger, "logger is required.");
    this.encoder = encoder;
    this.interceptors = (interceptors == null) ? Collections.emptyList() : interceptors;
    this.client = client;
    this.decoder = decoder;
    this.exceptionHandler = exceptionHandler;
    this.executor = executor;
    this.targetMethodDefinition = targetMethodDefinition;
    this.logger = logger;
    this.retry = retry;
  }

  /**
   * Execute the HttpRequest pipeline.
   *
   * @param arguments for the method.
   * @return the result of the request.
   */
  @Override
  public Object execute(final Object[] arguments) {

    /* prepare the request specification */
    final RequestSpecification requestSpecification = this.resolve(arguments);

    /* create an asynchronous chain, using the provided executor.  this implementation
     * is not non-blocking, however, it does try to take advantage of the executor provided.
     *
     * if the default executor is used, each one of these steps will be executed on the calling
     * thread and not on a different pool or on the default fork-join common pool, making
     * this call effectively synchronous.
     */
    CompletableFuture<Object> result =
        CompletableFuture.runAsync(() -> intercept(requestSpecification), this.executor)
            .thenRunAsync(() -> encode(requestSpecification, arguments), this.executor)
            .thenApplyAsync(nothing -> requestSpecification.build(), this.executor)
            .thenApplyAsync(this::request, this.executor)
            .handleAsync((response, throwable) -> {
              if (throwable != null) {
                /* dispatch to the error handler */
                log.error("Error occurred during method processing.  "
                        + "Passing to Exception Handler.  Exception: {}: {}",
                    throwable.getClass().getSimpleName(), throwable.getMessage());
                throw exceptionHandler.apply(throwable);
              } else {
                try {
                  /* decode the response */
                  return decode(response);
                } catch (Exception ex) {
                  log.error("Error occurred processing the response.  Passing to Exception Handler."
                      + "  Exception: {} {}", ex.getClass().getSimpleName(), ex.getMessage());
                  throw exceptionHandler.apply(ex);
                }
              }
            }, this.executor);

    /* delegate any additional handling to the sub classes. */
    return this.handleResponse(result);
  }

  /**
   * Process the results of the HttpRequest.
   *
   * @param result Future containing the results of the request.
   * @return the result of the request, decoded if necessary.
   */
  protected abstract Object handleResponse(CompletableFuture<Object> result);

  /**
   * Resolve any parameters and variables for this Request.
   *
   * @param arguments containing the values to use.
   * @return a {@link RequestSpecification} instance.
   */
  protected RequestSpecification resolve(Object[] arguments) {
    log.debug("Started processing of request: {}", this.targetMethodDefinition.getTag());
    RequestSpecification requestSpecification = this.targetMethodDefinition
        .requestSpecification(this.mapArguments(arguments));
    log.debug("UriTemplate resolved: {}", requestSpecification.uri());
    return requestSpecification;
  }

  /**
   * Apply any {@link RequestInterceptor}s.
   *
   * @param requestSpecification to intercept.
   */
  protected void intercept(RequestSpecification requestSpecification) {
    log.debug("Applying interceptors");
    for (RequestInterceptor interceptor : this.interceptors) {
      interceptor.accept(requestSpecification);
    }
  }

  /**
   * Determines which method argument contains the Request body, if any.
   *
   * @param arguments for the method.
   * @return the object to use as the request body, if any.
   */
  protected Optional<Object> getRequestBody(Object[] arguments) {
    Object body =
        (this.targetMethodDefinition.getBody() != -1) ? arguments[this.targetMethodDefinition
            .getBody()] : null;
    return Optional.ofNullable(body);
  }

  /**
   * Encode the Request Body, if required.
   *
   * @param requestSpecification to receive the encoded result.
   * @param arguments that may contain the request body.
   */
  protected void encode(RequestSpecification requestSpecification, Object[] arguments) {
    this.getRequestBody(arguments)
        .ifPresent(body -> {
          log.debug("Encoding Request Body: {}", body.getClass().getSimpleName());
          this.encoder.apply(body, requestSpecification);
        });
  }

  /**
   * Execute the Request on the Executor.
   *
   * @param request to request.
   * @return a {@link CompletableFuture} containing the Response.
   */
  protected Response request(final Request request) {
    try {
      this.logRequest(targetMethodDefinition.getTag(), request);
      final Response response =
          this.retry.execute(targetMethodDefinition.getTag(), request, req -> client.request(req));
      this.logResponse(targetMethodDefinition.getTag(), response);
      return response;
    } catch (Throwable th) {
      throw new FeignException(th.getMessage(), th, this.targetMethodDefinition.getTag());
    }
  }

  /**
   * Decode the Response.
   *
   * @param response to decode.
   * @return the desired decoded result
   */
  protected Object decode(Response response) {
    TypeDefinition typeDefinition = targetMethodDefinition.getReturnType();
    Class<?> returnType = typeDefinition.getType();
    if (void.class == returnType || (response == null || response.body() == null)) {
      return null;
    } else if (Response.class == returnType) {
      /* no need to decode */
      log.debug("Response type is feign.Response, no decoding necessary.");
      return response;
    } else {
      try {
        /* decode the response */
        log.debug("Decoding Response: {}", response);
        return this.decode(response, typeDefinition);
      } catch (Exception ex) {
        throw new FeignException(ex.getMessage(), ex, this.targetMethodDefinition.getTag());
      }
    }
  }

  /**
   * Decode the Response, using the TypeDefinition provided.
   *
   * @param response to decode.
   * @param typeDefinition to use to determine what the resulting type should be.
   * @return the response body decoded into the desired type.
   * @throws Exception if the {@link Response} cannot be closed after decoding.
   */
  private Object decode(Response response, TypeDefinition typeDefinition) throws Exception {
    /* before jumping into and just passing the raw type the decoder, there are
     * certain types that act as containers.  when decoding these types, we want to
     * decode the 'contained' type and then wrap the result in the desired container,
     */
    try {
      if (typeDefinition.isContainer()) {
        /* we want to decode the actual type */
        return this.decoder.decode(response, typeDefinition.getActualType());
      } else {
        return this.decoder.decode(response, typeDefinition.getType());
      }
    } finally {
      response.close();
    }
  }

  /**
   * Map the Argument list to any registered Template Parameters.
   *
   * @param arguments to map.
   * @return a new Map, where the argument matches corresponding Template parameter.
   */
  private Map<TemplateParameter, Object> mapArguments(Object[] arguments) {
    Map<TemplateParameter, Object> variables = new LinkedHashMap<>();
    for (int i = 0; i < arguments.length; i++) {
      final Object argument = arguments[i];
      Optional<TemplateParameter> templateParameter =
          this.targetMethodDefinition.getTemplateParameter(i);
      templateParameter.ifPresent(parameter -> variables.put(parameter, argument));
    }
    return variables;
  }

  /**
   * Log the Request.
   *
   * @param request to log.
   */
  protected void logRequest(String method, Request request) {
    this.logger.logRequest(method, request);
  }

  /**
   * Log the Response.
   *
   * @param response to log
   */
  protected void logResponse(String method, Response response) {
    this.logger.logResponse(method, response);
  }

}
