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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public abstract class StyleExpressionTest extends ExpressionTest {

  @Override
  @Test
  void expand_withSingleVariable() {
    Expression expression = this.getExpression("{var}", -1);
    assertThat(expression.getVariables()).hasSize(1);
    assertThat(expression.getLimit()).isEqualTo(-1);

    String result = expression.expand(Collections.singletonMap("var", var));
    assertThat(result).isEqualTo(expression.getPrefix() + "var=" + var);
  }

  @Override
  @Test
  void expand_withHalf() {
    Expression expression = this.getExpression("{half}", -1);
    String result = expression.expand(Collections.singletonMap("half", half));
    assertThat(result).isEqualTo(expression.getPrefix() + "half=50%25");
  }

  @Override
  @Test
  void expand_withBase() {
    Expression expression = this.getExpression("{base}", -1);
    String result = expression.expand(Collections.singletonMap("base", base));
    assertThat(result).isEqualTo(expression.getPrefix() + "base=http%3A%2F%2Fexample.com%2Fhome%2F");
  }

  @Override
  @Test
  void expand_withPath() {
    Expression expression = this.getExpression("{path}", -1);
    String result = expression.expand(Collections.singletonMap("path", path));
    assertThat(result).isEqualTo(expression.getPrefix() + "path=%2Ffoo%2Fbar");
  }

  @Override
  @Test
  public void expand_withMultipleVariables() {
    Expression expression = this.getExpression("{count}", -1);
    String result = expression.expand(Collections.singletonMap("count", count));
    assertThat(result).isEqualTo(expression.getPrefix() + "count=one,two,three");
  }

  @Override
  @Test
  void expand_withMultipleVariablesEncoded() {
    Expression expression = this.getExpression("{x,hello,y}", -1);
    assertThat(expression.getVariables()).hasSize(3);
    assertThat(expression.getLimit()).isEqualTo(-1);

    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("x", x);
    variables.put("hello", hello);
    variables.put("y", y);
    String result = expression.expand(variables);
    assertThat(result).isEqualTo(expression.getPrefix() + "x=" +x + expression.getDelimiter()
        + "hello=" + helloEncoded + expression.getDelimiter() + "y=" + y);
  }

  @Override
  @Test
  void expand_withRepeatedVariables() {
    Expression expression = this.getExpression("{who,who}", -1);
    assertThat(expression.getVariables()).hasSize(2);
    assertThat(expression.getLimit()).isEqualTo(-1);

    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("who", who);
    String result = expression.expand(variables);
    assertThat(result).isEqualTo(expression.getPrefix() + "who=" + who + expression.getDelimiter()
        + "who=" + who);
  }

  @Override
  @Test
  void expand_undefinedAreRemoved() {
    Expression expression = this.getExpression("{x,undef}", -1);

    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("x", x);
    variables.put("undef", undef);
    String result = expression.expand(variables);
    assertThat(result).isEqualTo(expression.getPrefix() + "x=" + x);
  }

  @Override
  @Test
  void expand_missingIsUndefined() {
    Expression expression = this.getExpression("{x,undef}", -1);

    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("x", x);
    String result = expression.expand(variables);
    assertThat(result).isEqualTo(expression.getPrefix() + "x=" + x);
  }

  @Override
  @Test
  void expand_mixedUndefinedAreRemoved() {
    Expression expression = this.getExpression("{x,undef,v}", -1);

    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("x", x);
    variables.put("undef", undef);
    variables.put("v", v);
    String result = expression.expand(variables);
    assertThat(result).isEqualTo(expression.getPrefix() + "x=" + x + expression.getDelimiter()
        + "v=" + v);
  }

  @Override
  @Test
  void expand_withLimit() {
    Expression expression = this.getExpression("{var}", 3);
    assertThat(expression.getVariables()).hasSize(1);
    assertThat(expression.getLimit()).isEqualTo(3);

    String result = expression.expand(Collections.singletonMap("var", var));
    assertThat(result).isEqualTo(expression.getPrefix() + "var=" + var.substring(0, 3));
  }

  @Override
  @Test
  void expand_withIterable() {
    Expression expression = this.getExpression("{list}", -1);

    String result = expression.expand(
        Collections.singletonMap("list", list));
    assertThat(result).isEqualTo(expression.getPrefix() + "list=red"
        + Expression.DEFAULT_DELIMITER + "green"
        + Expression.DEFAULT_DELIMITER + "blue");
  }

  @Override
  @Test
  void expand_withIterableExploded() {
    Expression expression = this.getExpression("{list*}", -1);

    String result = expression.expand(
        Collections.singletonMap("list", list));
    assertThat(result).isEqualTo(expression.getPrefix() + "list=red"
        + expression.getDelimiter() + "list=green"
        + expression.getDelimiter() + "list=blue");
  }

  @Override
  @Test
  void expand_withEmptyIterable() {
    Expression expression = this.getExpression("{list}", -1);
    String result = expression.expand(Collections.singletonMap("list", Collections.emptyList()));
    assertThat(result).isEmpty();
  }

  @Override
  @Test
  void expand_withEmptyIterableExploded() {
    Expression expression = this.getExpression("{list*}", -1);
    String result = expression.expand(Collections.singletonMap("list", Collections.emptyList()));
    assertThat(result).isEmpty();
  }

  @Override
  @Test
  void expand_withComplexVariableName_andPctEncodedList() {
    Expression expression = this.getExpression("{list[]}", -1);
    String result = expression.expand(
        Collections.singletonMap("list[]", list));
    assertThat(result).isEqualTo(expression.getPrefix() + "list%5B%5D=red"
        + Expression.DEFAULT_DELIMITER + "green"
        + Expression.DEFAULT_DELIMITER + "blue");
  }

  @Override
  @Test
  void expand_emptyAreKept() {
    Expression expression =  this.getExpression("{x,empty}", -1);

    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("x", x);
    variables.put("empty", empty);
    String result = expression.expand(variables);
    assertThat(result).isEqualTo(expression.getPrefix() + "x=" + x
        + expression.getDelimiter() + "empty=");
  }

  @Override
  @Test
  void expand_withValueAlreadyEncoded() {
    Expression expression = this.getExpression("{hello}", -1);
    String result = expression.expand(Collections.singletonMap("hello", helloEncoded));
    assertThat(result).isEqualTo(expression.getPrefix() + "hello=" + helloEncoded);
  }

  @Override
  @Test
  void expand_withMap() {
    Expression expression = this.getExpression("{keys}", -1);
    String result = expression.expand(Collections.singletonMap("keys", keys));
    assertThat(result).isEqualTo(expression.getPrefix()
        + "keys=semi" + Expression.DEFAULT_DELIMITER + "%3B"
        + Expression.DEFAULT_DELIMITER + "dot"
        + Expression.DEFAULT_DELIMITER + "."
        + Expression.DEFAULT_DELIMITER + "comma"
        + Expression.DEFAULT_DELIMITER + "%2C");
  }

  @Override
  @Test
  public void expand_withMapExploded() {
    Expression expression = this.getExpression("{keys*}", -1);
    String result = expression.expand(Collections.singletonMap("keys", keys));
    assertThat(result).isEqualTo(expression.getPrefix()
        + "semi=%3B" + expression.getDelimiter()
        + "dot=." + expression.getDelimiter()
        + "comma=%2C");
  }

  @Override
  @Test
  public void expand_withEmptyMap() {
    Expression expression = this.getExpression("{keys}", -1);
    String result = expression.expand(Collections.singletonMap("keys", emptyKeys));
    assertThat(result).isEmpty();
  }

  @Override
  @Test
  public void expand_withEmptyMapExploded() {
    Expression expression = this.getExpression("{keys*}", -1);
    String result = expression.expand(Collections.singletonMap("keys", emptyKeys));
    assertThat(result).isEmpty();
  }

}
