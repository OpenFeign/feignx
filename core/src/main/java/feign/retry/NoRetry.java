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

package feign.retry;

import feign.Request;
import feign.Response;
import feign.Retry;
import java.util.function.Function;

/**
 * Executes the callback without any retries.
 */
public class NoRetry implements Retry {

  @Override
  public Response execute(
      String methodName, Request request, Function<Request, Response> callback) {
    /* execute the callback with no retry support */
    return callback.apply(request);
  }
}
