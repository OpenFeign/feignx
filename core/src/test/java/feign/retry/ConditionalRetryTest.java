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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import feign.Client;
import feign.ExceptionHandler;
import feign.ExceptionHandler.RethrowExceptionHandler;
import feign.Logger;
import feign.Request;
import feign.Response;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConditionalRetryTest {

  @Mock
  private Logger logger;

  @Mock
  private Request request;

  @Mock
  private Client client;

  @Mock
  private Response response;

  private ExceptionHandler exceptionHandler = new RethrowExceptionHandler();

  private ConditionalRetry conditionalRetry;

  @Test
  void shouldStopRetry_andReturnLastResponse_whenConditionPass_andAttemptsExhausted()
      throws Throwable {
    RetryCondition condition = mock(RetryCondition.class);
    when(condition.test(any(RetryContext.class))).thenReturn(true);
    when(client.request(any(Request.class))).thenReturn(this.response);

    this.conditionalRetry =
        new ConditionalRetry(3, condition, retryContext -> 1L, logger, exceptionHandler);

    /* retry should run 3 times and then stop, returning the last result */
    Response response =
        this.conditionalRetry.execute("test", this.request, client::request);
    verify(this.client, times(3)).request(any(Request.class));
    verify(this.logger, times(2)).logRetry(anyString(), any(RetryContext.class));
    assertThat(response).isNotNull();
  }

  @Test
  void shouldStopRetry_andThrowLastException_whenConditionPass_andAttemptsExhausted() {
    RetryCondition condition = mock(RetryCondition.class);
    when(condition.test(any(RetryContext.class))).thenReturn(true);
    when(client.request(any(Request.class))).thenReturn(this.response, this.response)
        .thenThrow(new RuntimeException("failed"));

    this.conditionalRetry =
        new ConditionalRetry(3, condition, retryContext -> 1L, logger, exceptionHandler);

    /* retry should run 3 times and then stop, throwing the last exception */
    assertThrows(RuntimeException.class, () ->
        this.conditionalRetry.execute("test", this.request, client::request));
    verify(this.client, times(3)).request(any(Request.class));
    verify(this.logger, times(2)).logRetry(anyString(), any(RetryContext.class));
  }

  @Test
  void shouldNotRetry_whenConditionFails_onInitialAttempt() throws Throwable {
    RetryCondition condition = mock(RetryCondition.class);
    when(condition.test(any(RetryContext.class))).thenReturn(false);
    when(client.request(any(Request.class))).thenReturn(this.response);

    this.conditionalRetry =
        new ConditionalRetry(3, condition, retryContext -> 1L, logger, exceptionHandler);

    /* retry should not run at all */
    Response response =
        this.conditionalRetry.execute("test", this.request, client::request);
    verify(this.client, times(1)).request(any(Request.class));
    verifyZeroInteractions(this.logger);
    assertThat(response).isNotNull();
  }

  @Test
  void shouldNotRetry_whenConditionFails_afterInitialAttempt() throws Throwable {
    RetryCondition condition = mock(RetryCondition.class);
    when(condition.test(any(RetryContext.class))).thenReturn(true, false);
    when(client.request(any(Request.class))).thenReturn(this.response);

    this.conditionalRetry =
        new ConditionalRetry(3, condition, retryContext -> 1L, logger, exceptionHandler);

    /* retry should run 2 times and then stop, returning the last result */
    Response response =
        this.conditionalRetry.execute("test", this.request, client::request);
    verify(this.client, times(2)).request(any(Request.class));
    verify(this.logger, times(1)).logRetry(anyString(), any(RetryContext.class));
    assertThat(response).isNotNull();
  }

  @Test
  void shouldDelay_betweenRetries() throws Throwable {
    RetryCondition condition = mock(RetryCondition.class);
    when(condition.test(any(RetryContext.class))).thenReturn(true, false);

    RetryInterval retryInterval = mock(RetryInterval.class);
    when(retryInterval.getInterval(any(RetryContext.class))).thenReturn(1000L);
    when(client.request(any(Request.class))).thenReturn(this.response);

    this.conditionalRetry =
        new ConditionalRetry(3, condition, retryInterval, logger, exceptionHandler);

    /* retry should run 2 times and then stop, returning the last result */
    long start = System.nanoTime();
    Response response =
        this.conditionalRetry.execute("test", this.request, client::request);
    long end = System.nanoTime();

    /* verify we waited the appropriate time */
    assertThat(TimeUnit.NANOSECONDS.toMillis(end - start))
        .isGreaterThanOrEqualTo(1000);
    verify(this.client, times(2)).request(any(Request.class));
    verify(this.logger, times(1)).logRetry(anyString(), any(RetryContext.class));
    verify(retryInterval, times(1)).getInterval(any(RetryContext.class));
    assertThat(response).isNotNull();
  }

  @Test
  void shouldNotDelay_betweenRetries_withZeroDelay() throws Throwable {
    RetryCondition condition = mock(RetryCondition.class);
    when(condition.test(any(RetryContext.class))).thenReturn(true, false);

    RetryInterval retryInterval = mock(RetryInterval.class);
    when(retryInterval.getInterval(any(RetryContext.class))).thenReturn(0L);
    when(client.request(any(Request.class))).thenReturn(this.response);

    this.conditionalRetry =
        new ConditionalRetry(3, condition, retryInterval, logger, exceptionHandler);
    Response response =
        this.conditionalRetry.execute("test", this.request, client::request);

    /* verify we waited the appropriate time */
    verify(this.client, times(2)).request(any(Request.class));
    verify(this.logger, times(1)).logRetry(anyString(), any(RetryContext.class));
    verify(retryInterval, times(1)).getInterval(any(RetryContext.class));
    assertThat(response).isNotNull();
  }
}