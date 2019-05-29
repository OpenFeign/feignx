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

import org.junit.jupiter.api.Test;

class BackoffRetryIntervalTest {

  @Test
  void getInterval_shouldApplyMultiplier() {
    BackoffRetryInterval interval =
        new BackoffRetryInterval(1000, 2, 10000);

    /* should be 1000, 2000, 4000 */
    assertThat(interval.getInterval(new RetryContext(1, null, null)))
        .isEqualTo(1000);
    assertThat(interval.getInterval(new RetryContext(2, null, null)))
        .isEqualTo(2000);
    assertThat(interval.getInterval(new RetryContext(3, null, null)))
        .isEqualTo(4000);
  }

  @Test
  void getInterval_shouldNotExceedMax() {
    BackoffRetryInterval interval =
        new BackoffRetryInterval(1000, 2, 10000);

    /* should be 8, 10, and 10 */
    assertThat(interval.getInterval(new RetryContext(5, null, null)))
        .isEqualTo(8000);
    assertThat(interval.getInterval(new RetryContext(6, null, null)))
        .isEqualTo(10000);
    assertThat(interval.getInterval(new RetryContext(7, null, null)))
        .isEqualTo(10000);
  }
}