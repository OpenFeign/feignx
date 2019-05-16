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

import java.util.Collections;
import org.junit.jupiter.api.Test;

class DotExpressionTest extends SimpleExpressionTest {

  @Override
  protected Expression getExpression(String variableSpecification, int limit) {
    Expression expression = new DotExpression(variableSpecification);
    expression.setLimit(limit);
    return expression;
  }


  @Test
  void expand_domain() {
    Expression expression = this.getExpression("{dom*}", -1);
    String result = expression.expand(Collections.singletonMap("dom", dom));
    assertThat(result).isEqualTo(".example.com");
  }
}