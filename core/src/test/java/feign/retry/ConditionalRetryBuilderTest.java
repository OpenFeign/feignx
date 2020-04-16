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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import feign.ExceptionHandler.RethrowExceptionHandler;
import feign.Logger;
import feign.Retry;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class ConditionalRetryBuilderTest {

  @Test
  void build_withEveryCondition() {
    Retry retry = ConditionalRetry.builder(5, mock(Logger.class), new RethrowExceptionHandler())
        .interval(100L, TimeUnit.MILLISECONDS)
        .multiplier(2)
        .maxInterval(10, TimeUnit.SECONDS)
        .statusCode(409)
        .statusCode(503)
        .exception(IOException.class)
        .useRetryAfter()
        .build();
    assertThat(retry).isInstanceOf(ConditionalRetry.class);

    ConditionalRetry conditionalRetry = (ConditionalRetry) retry;
    assertThat(conditionalRetry.getInterval()).isInstanceOf(RetryAfterHeaderInterval.class);
    assertThat(conditionalRetry.getCondition()).isNotNull();
  }

  @Test
  void build_onlyExceptionCondition() {
    Retry retry = ConditionalRetry.builder(1, mock(Logger.class), new RethrowExceptionHandler())
        .exception(IOException.class)
        .build();
    assertThat(retry).isNotNull().isInstanceOf(ConditionalRetry.class);
    assertThat(((ConditionalRetry) retry).getCondition()).isInstanceOf(ExceptionCondition.class);

  }

  @Test
  void build_onlyStatusCondition() {
    Retry retry = ConditionalRetry.builder(1, mock(Logger.class),new RethrowExceptionHandler())
        .statusCode(503)
        .build();
    assertThat(retry).isNotNull().isInstanceOf(ConditionalRetry.class);
    assertThat(((ConditionalRetry) retry).getCondition()).isInstanceOf(StatusCodeCondition.class);
  }

  @Test
  void build_withCustomInterval() {
    Retry retry = ConditionalRetry.builder(1, mock(Logger.class), new RethrowExceptionHandler())
        .interval(retryContext -> 100)
        .condition(new StatusCodeCondition(502))
        .build();
    assertThat(retry).isNotNull().isInstanceOf(ConditionalRetry.class);
    assertThat(((ConditionalRetry) retry).getInterval()).isNotNull();
  }

  @Test
  void build_withCustomCondition() {
    Retry retry = ConditionalRetry.builder(1, mock(Logger.class), new RethrowExceptionHandler())
        .condition(context -> true)
        .build();
    assertThat(retry).isNotNull().isInstanceOf(ConditionalRetry.class);
    assertThat(((ConditionalRetry) retry).getCondition()).isNotNull();
  }

  @Test
  void build_withCustomCondition_withExisting() {
    Retry retry = ConditionalRetry.builder(1, mock(Logger.class), new RethrowExceptionHandler())
        .condition(new StatusCodeCondition(503))
        .condition(context -> true)
        .build();
    assertThat(retry).isNotNull().isInstanceOf(ConditionalRetry.class);
    assertThat(((ConditionalRetry) retry).getCondition()).isNotNull();
  }

  @Test
  void interval_cannotBeZero() {
    assertThrows(IllegalStateException.class,
        () -> ConditionalRetry.builder(1, mock(Logger.class), new RethrowExceptionHandler())
            .interval(0, TimeUnit.MILLISECONDS));
  }

  @Test
  void interval_cannotBeNull() {
    assertThrows(IllegalArgumentException.class,
        () -> ConditionalRetry.builder(1, mock(Logger.class), new RethrowExceptionHandler())
            .interval(null));
  }

  @Test
  void exception_cannotBeNull() {
    assertThrows(IllegalArgumentException.class,
        () -> ConditionalRetry.builder(1, mock(Logger.class), new RethrowExceptionHandler())
            .exception(null));
  }

  @Test
  void multiplier_cannotBeZero() {
    assertThrows(IllegalStateException.class,
        () -> ConditionalRetry.builder(1, mock(Logger.class), new RethrowExceptionHandler())
            .multiplier(0));
  }

  @Test
  void condition_cannotBeNull() {
    assertThrows(IllegalArgumentException.class,
        () -> ConditionalRetry.builder(1, mock(Logger.class), new RethrowExceptionHandler())
            .condition(null));
  }

  @Test
  void maxInterval_cannotBeZero() {
    assertThrows(IllegalStateException.class,
        () -> ConditionalRetry.builder(1, mock(Logger.class), new RethrowExceptionHandler())
            .maxInterval(0, TimeUnit.MILLISECONDS));
  }
}