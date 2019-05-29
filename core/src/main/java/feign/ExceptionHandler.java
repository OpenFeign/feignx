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

package feign;

import java.util.function.Function;

/**
 * Consumer responsible for processing any Exceptions that occur during method processing.
 */
public interface ExceptionHandler extends Function<Throwable, RuntimeException> {

  /**
   * Throws a new RuntimeException based on the exception received.
   *
   * @param throwable to throw again.
   */
  @Override
  default RuntimeException apply(Throwable throwable) {
    /* always rethrow */
    if (throwable instanceof RuntimeException) {
      return (RuntimeException) throwable;
    }
    return new RuntimeException(throwable);
  }

  /**
   * Exception Handler that wraps and throws any exceptions.
   */
  class RethrowExceptionHandler implements ExceptionHandler {

  }
}
