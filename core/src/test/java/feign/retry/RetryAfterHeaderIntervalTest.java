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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import feign.Response;
import feign.http.HttpHeader;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RetryAfterHeaderIntervalTest {

  @Mock
  private RetryInterval fallback;

  private RetryAfterHeaderInterval interval;

  @BeforeEach
  void setUp() {
    when(fallback.getInterval(any(RetryContext.class))).thenReturn(1000L, 2000L);
    this.interval = new RetryAfterHeaderInterval(fallback);
  }

  @Test
  void shouldReturnDelay_withRetryHeader_inSeconds() {
    Response response = mock(Response.class);
    when(response.headers()).thenReturn(
        Collections.singletonList(
            new HttpHeader("Retry-After", Collections.singletonList("120"))));

    RetryContext context = new RetryContext(1, null, response);
    long result = this.interval.getInterval(context);
    assertThat(result).isEqualTo(TimeUnit.SECONDS.toMillis(120));
  }

  @Test
  void shouldReturnDelay_withRetryHeader_inHttpDate() {
    String retryDate = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(120));
    Response response = mock(Response.class);
    when(response.headers()).thenReturn(
        Collections.singletonList(
            new HttpHeader("Retry-After", Collections.singletonList(retryDate))));

    RetryContext context = new RetryContext(1, null, response);
    long result = this.interval.getInterval(context);
    assertThat(result).isCloseTo(
        TimeUnit.SECONDS.toMillis(120), Percentage.withPercentage(1));
  }

  @Test
  void shouldReturnDelay_fromDefault_withoutHeader() {
    Response response = mock(Response.class);
    RetryContext context = new RetryContext(1, null, response);
    long result = this.interval.getInterval(context);
    assertThat(result).isEqualTo(1000L);
    verify(fallback, times(1)).getInterval(any(RetryContext.class));
  }

  @Test
  void shouldReturnDelay_fromDefault_ifHeaderHasNoValue () {
    Response response = mock(Response.class);
    when(response.headers()).thenReturn(
        Collections.singletonList(
            new HttpHeader("Retry-After", Collections.emptyList())));
    RetryContext context = new RetryContext(1, null, response);
    long result = this.interval.getInterval(context);
    assertThat(result).isEqualTo(1000L);
    verify(fallback, times(1)).getInterval(any(RetryContext.class));
  }

  @Test
  void shouldReturnDelay_fromDefault_onParsingDateError() {
    Response response = mock(Response.class);
    when(response.headers()).thenReturn(
        Collections.singletonList(
            new HttpHeader("Retry-After", Collections.singletonList("Not A Date"))));
    RetryContext context = new RetryContext(1, null, response);
    long result = this.interval.getInterval(context);
    assertThat(result).isEqualTo(1000L);
    verify(fallback, times(1)).getInterval(any(RetryContext.class));
  }
}