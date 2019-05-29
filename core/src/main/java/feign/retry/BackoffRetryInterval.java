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
 * Retry interval that uses an exponentially increasing interval.
 */
public class BackoffRetryInterval implements RetryInterval {

  private final long interval;
  private long multiplier;
  private long maxInterval;

  /**
   * Creates a new {@link BackoffRetryInterval}.
   *
   * @param interval to start with.
   * @param multiplier to multiply the interval with.
   * @param maxInterval to use.
   */
  public BackoffRetryInterval(long interval, long multiplier, long maxInterval) {
    this.interval = interval;
    this.multiplier = multiplier;
    this.maxInterval = maxInterval;
  }

  @Override
  public long getInterval(RetryContext retryContext) {
    if (retryContext.getAttempts() == 1) {
      return this.interval;
    }

    long interval = (retryContext.getAttempts() - 1) * (this.interval * this.multiplier);
    return (interval < this.maxInterval) ? interval : this.maxInterval;
  }
}
