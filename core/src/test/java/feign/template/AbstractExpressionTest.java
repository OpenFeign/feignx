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

package feign.template;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class AbstractExpressionTest {

  @Test
  void getValue_isNotNull() {
    AbstractExpression expression = new FormStyleExpression(Collections.singletonList("tests*"));
    assertThat(expression.getValue()).isNotNull()
        .isEqualToIgnoringCase("{?tests*}");
  }


  @Test
  void getValue_includedPrefix() {
    AbstractExpression expression = new FormStyleExpression(Collections.singletonList("tests:1"));
    assertThat(expression.getValue()).isNotNull()
        .isEqualToIgnoringCase("{?tests:1}");
  }

  @Test
  void getValue_isToString() {
    AbstractExpression expression = new FormStyleExpression(
        Arrays.asList("tests:1","again"));
    assertThat(expression.getValue()).isNotNull()
        .isEqualToIgnoringCase("{?tests:1,again}")
        .isEqualToIgnoringCase(expression.toString());
  }

  @Test
  void prefix_MustNotBe_Negative() {
    assertThrows(IllegalArgumentException.class,
        () -> new SimpleExpression(Collections.singletonList("tests:-1")));
  }

  @Test
  void prefix_MustBe_LessThan_10K() {
    assertThrows(IllegalArgumentException.class,
        () -> new SimpleExpression(Collections.singletonList("tests:100000")));
  }

  @Test
  void prefix_MustBe_A_Number() {
    assertThrows(IllegalArgumentException.class,
        () -> new SimpleExpression(Collections.singletonList("tests:abc")));
  }
}