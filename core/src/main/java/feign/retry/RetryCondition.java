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

/**
 * Condition Predicate that determines if the request should be retried.
 */
public interface RetryCondition {

  /**
   * Evaluate the context against this condition.
   *
   * @param context to evaluate.
   * @return {@literal true} if the condition is met, {@literal false} otherwise.
   */
  boolean test(RetryContext context);

  /**
   * Returns a composed {@link RetryCondition} logically OR between this {@link RetryCondition} and
   * the supplied condition.
   *
   * @param condition to be OR'd with.
   * @return the composed condition.
   */
  default RetryCondition or(final RetryCondition condition) {
    return (context -> test(context) || condition.test(context));
  }

  /**
   * Returns a composed {@link RetryCondition} logically AND between this {@link RetryCondition} and
   * the supplied condition.
   *
   * @param condition to be AND'd with.
   * @return the composed condition.
   */
  default RetryCondition and(final RetryCondition condition) {
    return (context -> test(context) && condition.test(context));
  }

  /**
   * Returns a {@link RetryCondition} where the original condition is inverted.
   *
   * @return the inverted condition.
   */
  default RetryCondition not() {
    return (context -> !test(context));
  }
}


