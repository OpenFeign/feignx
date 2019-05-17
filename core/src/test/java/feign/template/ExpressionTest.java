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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public abstract class ExpressionTest {

  /* Constants for each example.  Taken from RFC 6570 */
  static List<String> count = Arrays.asList("one", "two", "three");
  static List<String> dom = Arrays.asList("example", "com");
  static String dub = "me/too";
  static String dubEncoded = "me%2Ftoo";
  static String var = "value";
  static String hello = "Hello World!";
  static String helloEncoded = "Hello%20World%21";
  static String helloReserved = "Hello%20World!";
  static String half = "50%";
  static String who = "fred";
  static String base = "http://example.com/home/";
  static String path = "/foo/bar";
  static List<String> list = Arrays.asList("red", "green", "blue");
  static Map<String, String> keys = new LinkedHashMap<String, String>() {{
    put("semi", ";");
    put("dot", ".");
    put("comma", ",");
  }};
  static String v = "6";
  static String x = "1024";
  static String y = "768";
  static String empty = "";
  static Map<String, String> emptyKeys = Collections.emptyMap();
  static final String undef = null;

  protected abstract Expression getExpression(String variableSpecification, int limit);

  @Test
  void expand_withSingleVariable() {
    Expression expression = this.getExpression("{var}", -1);
    assertThat(expression.getVariables()).hasSize(1);
    assertThat(expression.getLimit()).isEqualTo(-1);
    assertThat(expression.getValue()).isEqualTo("{var}");

    String result = expression.expand(Collections.singletonMap("var", var));
    assertThat(result).isEqualTo(expression.getPrefix() + var);
  }

  @Test
  void expand_withHalf() {
    Expression expression = this.getExpression("{half}", -1);
    String result = expression.expand(Collections.singletonMap("half", half));
    assertThat(result).isEqualTo(expression.getPrefix() + "50%25");
  }

  @Test
  void expand_withBase() {
    Expression expression = this.getExpression("{base}", -1);
    String result = expression.expand(Collections.singletonMap("base", base));
    assertThat(result).isEqualTo(expression.getPrefix() + "http%3A%2F%2Fexample.com%2Fhome%2F");
  }

  @Test
  void expand_withPath() {
    Expression expression = this.getExpression("{path}", -1);
    String result = expression.expand(Collections.singletonMap("path", path));
    assertThat(result).isEqualTo(expression.getPrefix() + "%2Ffoo%2Fbar");
  }

  @Test
  public void expand_withMultipleVariables() {
    Expression expression = this.getExpression("{count}", -1);
    String result = expression.expand(Collections.singletonMap("count", count));
    assertThat(result).isEqualTo(expression.getPrefix() + "one,two,three");
  }

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
    assertThat(result).isEqualTo(expression.getPrefix() + x + expression.getDelimiter()
        + helloEncoded + expression.getDelimiter() + y);
  }

  @Test
  void expand_withRepeatedVariables() {
    Expression expression = this.getExpression("{who,who}", -1);
    assertThat(expression.getVariables()).hasSize(2);
    assertThat(expression.getLimit()).isEqualTo(-1);

    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("who", who);
    String result = expression.expand(variables);
    assertThat(result).isEqualTo(expression.getPrefix() + who + expression.getDelimiter()
        + who);
  }

  @Test
  void expand_undefinedAreRemoved() {
    Expression expression = this.getExpression("{x,undef}", -1);

    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("x", x);
    variables.put("undef", undef);
    String result = expression.expand(variables);
    assertThat(result).isEqualTo(expression.getPrefix() + x);
  }

  @Test
  void expand_missingIsUndefined() {
    Expression expression = this.getExpression("{x,undef}", -1);

    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("x", x);
    String result = expression.expand(variables);
    assertThat(result).isEqualTo(expression.getPrefix() + x);
  }

  @Test
  void expand_mixedUndefinedAreRemoved() {
    Expression expression = this.getExpression("{x,undef,v}", -1);

    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("x", x);
    variables.put("undef", undef);
    variables.put("v", v);
    String result = expression.expand(variables);
    assertThat(result).isEqualTo(expression.getPrefix() + x + expression.getDelimiter() + v);
  }

  @Test
  void expand_emptyAreKept() {
    Expression expression =  this.getExpression("{x,empty}", -1);

    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("x", x);
    variables.put("empty", empty);
    String result = expression.expand(variables);
    assertThat(result).isEqualTo(expression.getPrefix() + x + expression.getDelimiter());
  }

  @Test
  void expand_withLimit() {
    Expression expression = this.getExpression("{var}", 3);
    assertThat(expression.getVariables()).hasSize(1);
    assertThat(expression.getLimit()).isEqualTo(3);
    assertThat(expression.getValue()).isEqualTo("{var:3}");

    String result = expression.expand(Collections.singletonMap("var", var));
    assertThat(result).isEqualTo(expression.getPrefix() + var.substring(0, 3));
  }

  @Test
  void expand_withIterable() {
    Expression expression = this.getExpression("{list}", -1);

    String result = expression.expand(
        Collections.singletonMap("list", list));
    assertThat(result).isEqualTo(expression.getPrefix() + "red"
        + Expression.DEFAULT_DELIMITER + "green"
        + Expression.DEFAULT_DELIMITER + "blue");
  }

  @Test
  void expand_withIterableExploded() {
    Expression expression = this.getExpression("{list*}", -1);

    String result = expression.expand(
        Collections.singletonMap("list", list));
    assertThat(result).isEqualTo(expression.getPrefix() + "red"
        + expression.getDelimiter() + "green"
        + expression.getDelimiter() + "blue");
  }

  @Test
  void expand_withEmptyIterable() {
    Expression expression = this.getExpression("{list}", -1);
    String result = expression.expand(Collections.singletonMap("list", Collections.emptyList()));
    assertThat(result).isEmpty();
  }

  @Test
  void expand_withEmptyIterableExploded() {
    Expression expression = this.getExpression("{list*}", -1);
    String result = expression.expand(Collections.singletonMap("list", Collections.emptyList()));
    assertThat(result).isEmpty();
  }

  @Test
  void expand_withComplexVariableName_andPctEncodedList() {
    Expression expression = this.getExpression("{list[]}", -1);
    String result = expression.expand(
        Collections.singletonMap("list[]", list));
    assertThat(result).isEqualTo(expression.getPrefix() + "red"
        + Expression.DEFAULT_DELIMITER + "green"
        + Expression.DEFAULT_DELIMITER + "blue");
  }

  @Test
  void expand_withValueAlreadyEncoded() {
    Expression expression = this.getExpression("{hello}", -1);
    String result = expression.expand(Collections.singletonMap("hello", helloEncoded));
    assertThat(result).isEqualTo(expression.getPrefix() + helloEncoded);
  }

  @Test
  void expand_withMap() {
    Expression expression = this.getExpression("{keys}", -1);
    String result = expression.expand(Collections.singletonMap("keys", keys));
    assertThat(result).isEqualTo(expression.getPrefix()
        + "semi" + Expression.DEFAULT_DELIMITER + "%3B"
        + Expression.DEFAULT_DELIMITER + "dot"
        + Expression.DEFAULT_DELIMITER + "."
        + Expression.DEFAULT_DELIMITER + "comma"
        + Expression.DEFAULT_DELIMITER + "%2C");
  }

  @Test
  public void expand_withMapExploded() {
    Expression expression = this.getExpression("{keys*}", -1);
    String result = expression.expand(Collections.singletonMap("keys", keys));
    assertThat(result).isEqualTo(expression.getPrefix()
        + "semi=%3B" + expression.getDelimiter()
        + "dot=." + expression.getDelimiter()
        + "comma=%2C");
  }

  @Test
  public void expand_withEmptyMap() {
    Expression expression = this.getExpression("{keys}", -1);
    String result = expression.expand(Collections.singletonMap("keys", emptyKeys));
    assertThat(result).isEmpty();
  }

  @Test
  public void expand_withEmptyMapExploded() {
    Expression expression = this.getExpression("{keys*}", -1);
    String result = expression.expand(Collections.singletonMap("keys", emptyKeys));
    assertThat(result).isEmpty();
  }


  @Test
  void expand_withLimitOnMap_isNotAllowed() {
    Expression expression = this.getExpression("{keys}", 10);
    assertThrows(IllegalStateException.class,
        () -> expression.expand(Collections.singletonMap("keys", keys)));
  }

  @Test
  void expand_withLimitOnList_isNotAllowed() {
    Expression expression = this.getExpression("{list}", 10);
    assertThrows(IllegalStateException.class,
        () -> expression.expand(Collections.singletonMap("list", keys)));
  }

}
