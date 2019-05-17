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
import feign.RequestEncoder;
import feign.RequestInterceptor;
import feign.Response;
import feign.ResponseDecoder;
import feign.TargetMethodDefinition;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

/**
 * Method Handler that deals with asynchronous return types, such as {@link Future}.
 */
public class AsyncTargetMethodHandler extends AbstractTargetMethodHandler {

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
   * @param logger for logging requests and responses.
   */
  AsyncTargetMethodHandler(TargetMethodDefinition targetMethodDefinition,
      RequestEncoder encoder, List<RequestInterceptor> interceptors,
      Client client, ResponseDecoder decoder, ExceptionHandler exceptionHandler,
      Executor executor, Logger logger) {
    super(targetMethodDefinition, encoder, interceptors, client, decoder, exceptionHandler,
        executor, logger);
  }

  /**
   * Handles the results of the Request execution by wrapping the result in a new {@link
   * CompletableFuture} containing the decoded Response body.  The method handler's Executor is used
   * for this future.
   *
   * @param response Future containing the results of the request.
   * @return a {@link CompletableFuture} reference wrapping the results.
   */
  @Override
  protected Object handleResponse(CompletableFuture<Response> response) {
    /* handle the result of the future */
    return response.handle((resp, throwable) -> {
      if (throwable != null) {
        /* invoke the exception handler here, as it will not be thrown to the parent. */
        getExceptionHandler().accept(throwable);
      } else {
        return decode(resp);
      }
      return null;
    });
  }
}
