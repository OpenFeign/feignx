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
import feign.exception.FeignException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * HttpMethod handler that uses the calling thread to process the request and response.
 */
public class BlockingTargetMethodHandler extends AbstractTargetMethodHandler {

  /**
   * Creates a new {@link BlockingTargetMethodHandler}.
   *
   * @param targetMethodDefinition containing the method configuration.
   * @param configuration          with the target configuration.
   */
  BlockingTargetMethodHandler(TargetMethodDefinition targetMethodDefinition,
      FeignConfiguration configuration) {
    /* create a new method handler, with a synchronous executor */
    super(targetMethodDefinition, configuration);
  }

  /**
   * Blocks the calling thread, waiting for the result of the request.
   *
   * @param result being processed.
   * @return the decoded response.
   */
  @Override
  protected Object handleResponse(CompletableFuture<Object> result) {
    try {
      /* pull the result of the task immediately, waiting for it to complete */
      return result.get();
    } catch (ExecutionException eex) {
      /* an error occurred.  by this point the error handler should have been called
       * already, check the cause of the exception and throw it higher.
       */
      Throwable cause = eex.getCause();

      /* just rethrow, the exception handler must throw a RuntimeException since it has
       * no checked exceptions in it's signature. */
      throw (RuntimeException) cause;

    } catch (InterruptedException ie) {
      /* the request was interrupted */
      throw new FeignException(ie.getMessage(), ie, this.targetMethodDefinition.getTag());
    }
  }
}
