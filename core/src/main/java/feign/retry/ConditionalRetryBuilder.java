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

import feign.ExceptionHandler;
import feign.Logger;
import feign.Retry;
import feign.support.Assert;
import java.util.concurrent.TimeUnit;

/**
 * Builder for creating {@link Retry} instances.
 */
public class ConditionalRetryBuilder {

  private Logger logger;
  private int maxAttempts;
  private long interval;
  private long multiplier = 1;
  private long maxInterval;
  private boolean useRetryAfter = false;
  private RetryInterval retryInterval;
  private RetryCondition retryCondition;
  private ExceptionHandler exceptionHandler;

  /**
   * Creates a new Builder.
   *
   * @param maxAttempts to retry.
   * @param logger instance.
   * @param exceptionHandler for handling exceptions during a retry.
   */
  ConditionalRetryBuilder(int maxAttempts, Logger logger, ExceptionHandler exceptionHandler) {
    this.maxAttempts = maxAttempts;
    this.logger = logger;
    this.exceptionHandler = exceptionHandler;
  }

  /**
   * Response status code that should trigger a retry.
   *
   * @param statusCode to check for.
   * @return the builder chain.
   */
  public ConditionalRetryBuilder statusCode(int statusCode) {
    RetryCondition condition = new StatusCodeCondition(statusCode);
    if (retryCondition == null) {
      this.retryCondition = condition;
    } else {
      this.retryCondition = this.retryCondition.or(condition);
    }
    return this;
  }

  /**
   * An exception that, if thrown, should trigger a retry.
   *
   * @param throwable to check for.
   * @return the builder chain.
   */
  public ConditionalRetryBuilder exception(Class<? extends Throwable> throwable) {
    Assert.isNotNull(throwable, "throwable must not be null");
    RetryCondition condition = new ExceptionCondition(throwable);
    if (retryCondition == null) {
      this.retryCondition = condition;
    } else {
      this.retryCondition = this.retryCondition.or(condition);
    }
    return this;
  }

  /**
   * Amount of time to wait between retries.
   *
   * @param interval time to wait.
   * @param timeUnit of the interval.
   * @return the builder chain.
   */
  public ConditionalRetryBuilder interval(long interval, TimeUnit timeUnit) {
    Assert.isTrue(interval, value -> value > 0, "interval must not be zero");
    this.interval = timeUnit.toMillis(interval);
    this.maxInterval = this.interval;
    return this;
  }

  /**
   * {@link RetryInterval} instance to use.
   *
   * @param interval instance to use.
   * @return the builder chain.
   */
  public ConditionalRetryBuilder interval(RetryInterval interval) {
    Assert.isNotNull(interval, "interval must not be null");
    this.retryInterval = interval;
    return this;
  }

  /**
   * Multiplier to apply to the interval on each subsequent retry, increasing the interval
   * exponentially each retry.  Use caution here, a large multiplier will quickly raise the
   * interval into the second and even minute range.  A good value is usually less than 2.
   *
   * @param multiplier to apply to the interval.
   * @return the builder chain.
   */
  public ConditionalRetryBuilder multiplier(long multiplier) {
    Assert.isTrue(multiplier, value -> value > 0, "multiplier must not be zero");
    this.multiplier = multiplier;
    return this;
  }

  /**
   * The maximum amount of time to wait between retries.
   *
   * @param maxInterval amount to wait
   * @param timeUnit for the interval.
   * @return the builder chain.
   */
  public ConditionalRetryBuilder maxInterval(long maxInterval, TimeUnit timeUnit) {
    Assert.isTrue(maxInterval, value -> value > 0, "maxInterval must not be zero");
    this.maxInterval = timeUnit.toMillis(maxInterval);
    return this;
  }

  /**
   * Enables support for the "Retry-After" HTTP Header.
   *
   * @return the builder chain.
   */
  public ConditionalRetryBuilder useRetryAfter() {
    this.useRetryAfter = true;
    return this;
  }

  /**
   * Register a custom condition.
   *
   * @param condition to register.
   * @return the builder chain.
   */
  public ConditionalRetryBuilder condition(RetryCondition condition) {
    Assert.isNotNull(condition, "condition must not be null");
    if (this.retryCondition == null) {
      this.retryCondition = condition;
    } else {
      this.retryCondition = this.retryCondition.or(condition);
    }
    return this;
  }

  /**
   * Build the Conditional Retry instance.
   *
   * @return a Conditional Retry instance.
   */
  public Retry build() {
    /* create the interval */
    if (this.retryInterval == null) {
      this.retryInterval =
          new BackoffRetryInterval(this.interval, this.multiplier, this.maxInterval);
      if (this.useRetryAfter) {
        /* replace the interval with one that supports the retry after header */
        this.retryInterval = new RetryAfterHeaderInterval(
            new BackoffRetryInterval(this.interval, this.multiplier, this.maxInterval));
      }
    }

    /* create a new conditional retry */
    return new ConditionalRetry(
        this.maxAttempts, this.retryCondition, this.retryInterval, this.logger,
        this.exceptionHandler);
  }
}
