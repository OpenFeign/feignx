/*
 * Copyright 2019-2020 OpenFeign Contributors
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

import feign.Response;
import java.util.Optional;

/**
 * The state of the current Retry attempt.
 */
public final class RetryContext {

  private final int attempts;
  private final Throwable lastException;
  private final Response response;

  /**
   * Creates a new {@link RetryContext}.
   *
   * @param attempts made.
   * @param lastException that occurred, if any.
   * @param response that was returned, if any.
   */
  public RetryContext(int attempts, Throwable lastException, Response response) {
    this.attempts = attempts;
    this.lastException = lastException;
    this.response = response;
  }

  public int getAttempts() {
    return this.attempts;
  }

  public Optional<Throwable> getLastException() {
    return Optional.ofNullable(this.lastException);
  }

  public Optional<Response> getResponse() {
    return Optional.ofNullable(this.response);
  }
}
