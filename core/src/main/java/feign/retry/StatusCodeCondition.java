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

import feign.support.Assert;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Retry Condition that determines if the status code on the Response should trigger a retry.
 */
public class StatusCodeCondition implements RetryCondition {

  private final Integer statusCode;

  /**
   * Creates a new {@link StatusCodeCondition}.
   *
   * @param statusCode that should trigger a retry.
   */
  public StatusCodeCondition(Integer statusCode) {
    Assert.isNotNull(statusCode, "statusCode must not be null.");
    this.statusCode = statusCode;
  }

  /**
   * Determines if the status code on the most recent Response should trigger a retry.
   *
   * @param retryContext to test.
   * @return {@literal true} if the status code should trigger a retry, {@literal false} otherwise.
   */
  @Override
  public boolean test(final RetryContext retryContext) {
    return retryContext.getResponse()
        .map(response -> statusCode.equals(response.status()))
        .orElse(false);
  }
}
