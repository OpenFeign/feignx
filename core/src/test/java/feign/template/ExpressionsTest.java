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

import org.junit.jupiter.api.Test;

class ExpressionsTest {

  @Test
  void create_simpleExpression() {
    assertThat(Expressions.create("{simple}")).isInstanceOf(SimpleExpression.class);
  }

  @Test
  void create_simpleExpressionWithLimit() {
    Expression expression = Expressions.create("{simple:3}");
    assertThat(expression).isInstanceOf(SimpleExpression.class);
    assertThat(expression.getLimit()).isEqualTo(3);
  }

  @Test
  void create_simpleExpressionExploded() {
    Expression expression = Expressions.create("{simple*}");
    assertThat(expression).isInstanceOf(SimpleExpression.class);
    assertThat(expression.hasExplodeModifier()).isTrue();
  }

  @Test
  void create_simpleExpressionLimitAndExploded() {
    Expression expression = Expressions.create("{simple*:3}");
    assertThat(expression).isInstanceOf(SimpleExpression.class);
    assertThat(expression.hasExplodeModifier()).isTrue();
    assertThat(expression.getLimit()).isEqualTo(3);
  }

  @Test
  void create_expressionWithModifierAndLimitAndExploded() {
    Expression expression = Expressions.create("{+simple*:3}");
    assertThat(expression).isInstanceOf(ReservedExpression.class);
    assertThat(expression.hasExplodeModifier()).isTrue();
    assertThat(expression.getLimit()).isEqualTo(3);
  }

  @Test
  void create_reservedExpression() {
    assertThat(Expressions.create("{+simple}")).isInstanceOf(ReservedExpression.class);
  }

  @Test
  void create_dotExpression() {
    assertThat(Expressions.create("{.simple}")).isInstanceOf(DotExpression.class);
  }

  @Test
  void create_pathSegmentExpression() {
    assertThat(Expressions.create("{/simple}")).isInstanceOf(PathSegmentExpression.class);
  }

  @Test
  void create_pathStyleExpression() {
    assertThat(Expressions.create("{;simple}")).isInstanceOf(PathStyleExpression.class);
  }

  @Test
  void create_formStyleExpression() {
    assertThat(Expressions.create("{?simple}")).isInstanceOf(FormStyleExpression.class);
  }

  @Test
  void create_formContStyleExpression() {
    assertThat(Expressions.create("{&simple}")).isInstanceOf(FormContinuationStyleExpression.class);
  }

}