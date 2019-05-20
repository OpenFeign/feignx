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

import feign.retry.RetryContext;

/**
 * Logger component responsible for log messages pertaining to specific components involved
 * in the Request/Response process.
 */
public interface Logger {

  /**
   * Log the {@link Request}.
   *
   * @param methodName of the method making the request.
   * @param request to log.
   */
  void logRequest(String methodName, Request request);

  /**
   * Log the {@link Response}.
   *
   * @param methodName of the method making the request.
   * @param response to log.
   */
  void logResponse(String methodName, Response response);

  /**
   * Log the {@link RetryContext} used during a retry.
   *
   * @param methodName of the method making the request.
   * @param context to log.
   */
  void logRetry(String methodName, RetryContext context);

}
