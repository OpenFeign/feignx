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

import feign.FeignConfiguration;
import feign.TargetMethodDefinition;
import feign.TargetMethodHandler;
import feign.TargetMethodHandlerFactory;
import java.util.concurrent.Future;

/**
 * Target Method Handler Factory that uses the {@link TargetMethodDefinition#getReturnType()}
 * to determine which Method Handler to create.
 */
public class TypeDrivenMethodHandlerFactory implements TargetMethodHandlerFactory {

  /**
   * Creates a new {@link TargetMethodHandler} based on the return type of the
   * {@link TargetMethodDefinition} provided.
   *
   * @param targetMethodDefinition to inspect.
   * @param configuration with the required dependencies.
   * @return a new {@link TargetMethodHandler} instance.
   */
  @Override
  public TargetMethodHandler create(TargetMethodDefinition targetMethodDefinition,
      FeignConfiguration configuration) {

    Class<?> returnType = targetMethodDefinition.getReturnType().getType();
    if (isFuture(returnType)) {
      return new AsyncTargetMethodHandler(
          targetMethodDefinition,
          configuration.getRequestEncoder(),
          configuration.getRequestInterceptors(),
          configuration.getClient(),
          configuration.getResponseDecoder(),
          configuration.getExceptionHandler(),
          configuration.getExecutor(),
          configuration.getLogger(),
          configuration.getRetry());
    } else {
      /* return a blocking handler */
      return new BlockingTargetMethodHandler(
          targetMethodDefinition,
          configuration.getRequestEncoder(),
          configuration.getRequestInterceptors(),
          configuration.getClient(),
          configuration.getResponseDecoder(),
          configuration.getExceptionHandler(),
          configuration.getExecutor(),
          configuration.getLogger(),
          configuration.getRetry());
    }
  }

  /**
   * Determines if the type provided is an implementation of a {@link Future}.
   *
   * @param type to evaluate.
   * @return {@literal true} if the type implements {@link Future}, {@literal false} otherwise.
   */
  private boolean isFuture(Class<?> type) {
    return Future.class.isAssignableFrom(type);
  }
}
