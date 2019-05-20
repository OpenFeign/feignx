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

class RetryConditionTest {

  private final RetryCondition alwaysTrue = context -> true;
  private final RetryCondition alwaysFalse = context -> false;

  @Test
  void or() {
    RetryContext context = new RetryContext(1, null, null);
    RetryCondition trueCondition = alwaysTrue.or(alwaysFalse);
    RetryCondition falseCondition = alwaysFalse.or(alwaysFalse);

    /* logical or */
    assertThat(trueCondition.test(context)).isTrue();
    assertThat(falseCondition.test(context)).isFalse();
  }

  @Test
  void and() {
    RetryContext context = new RetryContext(1, null, null);
    RetryCondition falseCondition = alwaysTrue.and(alwaysFalse);
    RetryCondition trueCondition = alwaysTrue.and(alwaysTrue);

    /* logical and */
    assertThat(falseCondition.test(context)).isFalse();
    assertThat(trueCondition.test(context)).isTrue();
  }

  @Test
  void negate() {
    RetryContext context = new RetryContext(1, null, null);
    RetryCondition negative = alwaysTrue.not();

    assertThat(negative.test(context)).isFalse();
    assertThat(alwaysFalse.not().test(context)).isTrue();
  }

}