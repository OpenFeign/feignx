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

import feign.support.Assert;
import feign.support.StringUtils;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retry Interval that uses the Retry-After HTTP Header to determine how long to wait.  If no header
 * is present, the fallback interval will be used.
 */
public class RetryAfterHeaderInterval implements RetryInterval {

  private static final Logger log = LoggerFactory.getLogger(RetryAfterHeaderInterval.class);
  private static final String RETRY_AFTER_HEADER = "Retry-After";
  private final RetryInterval fallback;

  /**
   * Creates a new {@link RetryAfterHeaderInterval}.
   *
   * @param fallback interval to use when the Header is not present.
   */
  public RetryAfterHeaderInterval(RetryInterval fallback) {
    Assert.isNotNull(fallback, "fallback cannot be required.");
    this.fallback = fallback;
  }

  @Override
  public long getInterval(final RetryContext retryContext) {
    /* look for the retry after header */
    return retryContext.getResponse()
        .map(response -> response.headers()
            .stream()
            .filter(header -> RETRY_AFTER_HEADER.equalsIgnoreCase(header.name())
                && header.values() != null
                && !header.values().isEmpty())
            .map(header -> {
              /* determine the wait time based on the retry header date */
              String headerValue = header.values().iterator().next();
              return determineInterval(headerValue);
            })
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(fallback.getInterval(retryContext))) // default if header is missing
        .orElseGet(() -> this.fallback.getInterval(retryContext));
  }


  /**
   * Determines the interval based on the value of the Retry-After Header.
   *
   * @param retryAfterHeaderValue to inspect.
   * @return the amount of time, in milliseconds, to wait before retrying.
   */
  private Long determineInterval(String retryAfterHeaderValue) {
    /* the Retry-After header comes in two flavors, a duration in seconds, or a
     * HTTP-DATE
     */
    if (StringUtils.isNumeric(retryAfterHeaderValue)) {
      /* the value is a delay in seconds, convert to milliseconds and retry */
      return TimeUnit.SECONDS.toMillis(Long.parseLong(retryAfterHeaderValue));
    } else {
      try {
        /* assume that the value is an HTTP Date, parse the date */
        ZonedDateTime retryDateTime = ZonedDateTime
            .parse(retryAfterHeaderValue, DateTimeFormatter.RFC_1123_DATE_TIME);

        /* calculate the amount of time between now and the retry date to determine the
         * required interval.
         */
        return Math.abs(
            ChronoUnit.MILLIS.between(retryDateTime, ZonedDateTime.now(ZoneOffset.UTC)));
      } catch (DateTimeParseException dtpe) {
        /* it's not a valid HTTP date, use the default interval */
        log.info(
            "Error occurred parsing the Retry-After Header Date value {}.  Fallback will be used.",
            retryAfterHeaderValue);
        return null;
      }
    }
  }

}
