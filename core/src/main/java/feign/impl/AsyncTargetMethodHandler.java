/*
 * Copyright 2019-2021 OpenFeign Contributors
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

import feign.FeignConfiguration;
import feign.contract.TargetMethodDefinition;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Method Handler that deals with asynchronous return types, such as {@link Future}.
 */
public class AsyncTargetMethodHandler extends AbstractTargetMethodHandler {

  /**
   * Creates a new Abstract Target HttpMethod Handler.
   *
   * @param targetMethodDefinition containing the method configuration.
   * @param configuration with the target configuration.
   */
  AsyncTargetMethodHandler(TargetMethodDefinition targetMethodDefinition,
      FeignConfiguration configuration) {
    super(targetMethodDefinition, configuration);
  }

  /**
   * Handles the results of the Request execution by wrapping the result in a new {@link
   * CompletableFuture} containing the decoded Response body.  The method handler's Executor is used
   * for this future.
   *
   * @param result Future containing the results of the request.
   * @return a {@link CompletableFuture} reference wrapping the results.
   */
  @Override
  protected Object handleResponse(CompletableFuture<Object> result) {
    /* let the caller decide what to do with it */
    return result;
  }
}
