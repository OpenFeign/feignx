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

import feign.ExceptionHandler;
import feign.Logger;
import feign.Request;
import feign.Response;
import feign.Retry;
import feign.support.Assert;
import java.util.function.Function;
import org.slf4j.LoggerFactory;

/**
 * The result of the callback is evaluated against a {@link RetryCondition} to determine if the
 * callback should be retried.
 */
public class ConditionalRetry implements Retry {

  private static final org.slf4j.Logger log = LoggerFactory.getLogger(ConditionalRetry.class);
  private int maxAttempts;
  private RetryCondition condition;
  private RetryInterval interval;
  private Logger logger;
  private ExceptionHandler exceptionHandler;

  public static ConditionalRetryBuilder builder(
      int maxAttempts, Logger logger, ExceptionHandler exceptionHandler) {
    return new ConditionalRetryBuilder(maxAttempts, logger, exceptionHandler);
  }

  /**
   * Creates a new {@link ConditionalRetry}.
   *
   * @param maxAttempts to try.
   * @param condition test to apply the result.
   * @param interval to wait between retries.
   * @param logger to log the result of the conditions.
   */
  ConditionalRetry(
      int maxAttempts, RetryCondition condition, RetryInterval interval, Logger logger,
      ExceptionHandler exceptionHandler) {
    Assert.isNotNull(logger, "logger must not be null");
    Assert.isNotNull(exceptionHandler, "exceptionHandler must not be null");
    Assert.isNotNull(condition, "condition must not be null");
    Assert.isNotNull(interval, "interval must not be null");
    this.maxAttempts = maxAttempts;
    this.condition = condition;
    this.interval = interval;
    this.logger = logger;
    this.exceptionHandler = exceptionHandler;
  }

  /**
   * Execute the callback, evaluating the result against the condition, waiting for the specified
   * interval, if a retry is required.  If a retry is required, this method will continue to retry
   * until one of the following conditions is {@literal true}
   *
   * <ul>
   *   <li>The condition is no longer satisfied - subsequent attempt(s) was successful.</li>
   *   <li>Number of attempts exceed the maximum allotted - subsequent retries were
   *   unsuccessful</li>
   * </ul>
   * <p>
   *   At this point, the function will return either the last exception thrown, in the event
   *   that that the last attempt ended with an exception, or the most recent Response received.
   * </p>
   *
   * @param methodName where this retry is being used.
   * @param request to use.
   * @param callback to manage.
   * @return the result of the callback.
   * @throws Throwable if an error occurred.
   */
  @Override
  public Response execute(
      String methodName, Request request, Function<Request, Response> callback) throws Throwable {
    int attempts = 0;
    Response response = null;
    Throwable lastException = null;
    boolean shouldRetry = true;

    /* continue to retry until told otherwise */
    while (shouldRetry) {
      attempts++;
      try {
        /* execute the callback */
        response = callback.apply(request);
      } catch (Exception ex) {
        /* keep track of the exception */
        lastException = this.exceptionHandler.apply(ex);
      }

      /* determine if we should retry this request */
      RetryContext context = new RetryContext(attempts, lastException, response);
      shouldRetry = this.condition.test(context) && (attempts < this.maxAttempts);

      if (shouldRetry) {
        /* wait the specified interval before trying again */
        this.logger.logRetry(methodName, context);
        long interval = this.interval.getInterval(context);
        if (interval > 0) {
          try {
            Thread.sleep(interval);
          } catch (InterruptedException ie) {
            /* this short circuited the retry, log that the interval was cut short */
            log.info("Retry interval was interrupted.  Starting next retry attempt.");
          }
        }
      }
    }

    if (lastException != null) {
      /* retry exhausted with an exception, throw the exception */
      throw lastException;
    }

    /* return the response */
    return response;
  }

  RetryCondition getCondition() {
    return condition;
  }

  RetryInterval getInterval() {
    return interval;
  }
}
