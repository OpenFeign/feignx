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
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class ReservedExpressionTest extends ExpressionTest {

  @Override
  @Test
  protected Expression getExpression(String variableSpecification, int limit) {
    Expression expression = new ReservedExpression(variableSpecification);
    expression.setLimit(limit);
    return expression;
  }

  @Override
  @Test
  public void expand_withMap() {
    Expression expression = this.getExpression("{keys}", -1);
    String result = expression.expand(Collections.singletonMap("keys", keys));
    assertThat(result).isEqualTo(expression.getPrefix()
        + "semi" + expression.getDelimiter() + ";" + expression.getDelimiter()
        + "dot" + expression.getDelimiter() + "." + expression.getDelimiter()
        + "comma" + expression.getDelimiter() + ",");
  }

  @Override
  @Test
  public void expand_withMapExploded() {
    Expression expression = this.getExpression("{keys*}", -1);
    String result = expression.expand(Collections.singletonMap("keys", keys));
    assertThat(result).isEqualTo(expression.getPrefix()
        + "semi=;" + expression.getDelimiter()
        + "dot=." + expression.getDelimiter()
        + "comma=,");
  }

  @Override
  @Test
  public void expand_withMultipleVariablesEncoded() {
    Expression expression = this.getExpression("{x,hello,y}", -1);
    assertThat(expression.getVariables()).hasSize(3);
    assertThat(expression.getLimit()).isEqualTo(-1);

    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("x", x);
    variables.put("hello", hello);
    variables.put("y", y);
    String result = expression.expand(variables);
    assertThat(result).isEqualTo(expression.getPrefix() + x
        + expression.getDelimiter() + helloReserved
        + expression.getDelimiter() + y);
  }

  @Override
  @Test
  public void expand_withBase() {
    Expression expression = this.getExpression("{base}", -1);
    String result = expression.expand(Collections.singletonMap("base", base));
    assertThat(result).isEqualTo(expression.getPrefix() + "http://example.com/home/");
  }

  @Override
  @Test
  public void expand_withPath() {
    Expression expression = this.getExpression("{path}", -1);
    String result = expression.expand(Collections.singletonMap("path", path));
    assertThat(result).isEqualTo(expression.getPrefix() + "/foo/bar");
  }
}
