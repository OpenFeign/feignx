package feign.template;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SimpleExpressionTest {

  @Test
  void expand_withSingleVariable() {
    SimpleExpression simpleExpression = new SimpleExpression("{var}");
    assertThat(simpleExpression.getVariables()).hasSize(1);
    assertThat(simpleExpression.getLimit()).isEqualTo(-1);

    String result = simpleExpression.expand(Collections.singletonMap("var", "value"));
    assertThat(result).isEqualTo("value");
  }

  @Test
  void expand_withMultipleVariables() {
    SimpleExpression simpleExpression = new SimpleExpression("{x,hello,y}");
    assertThat(simpleExpression.getVariables()).hasSize(3);
    assertThat(simpleExpression.getLimit()).isEqualTo(-1);

    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("x", "1024");
    variables.put("hello", "Hello World!");
    variables.put("y", "768");
    String result = simpleExpression.expand(variables);
    assertThat(result).isEqualTo("1024,Hello%20World%21,768");
  }

  @Test
  void expand_undefinedAreRemoved() {
    SimpleExpression simpleExpression = new SimpleExpression("{x,undef}");

    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("x", "1024");
    String result = simpleExpression.expand(variables);
    assertThat(result).isEqualTo("1024");
  }

  @Test
  void expand_emptyAreKept() {
    SimpleExpression simpleExpression = new SimpleExpression("{x,empty}");

    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("x", "1024");
    variables.put("empty", "");
    String result = simpleExpression.expand(variables);
    assertThat(result).isEqualTo("1024,");
  }

  @Test
  void expand_withLimit() {
    SimpleExpression simpleExpression = new SimpleExpression("{var}", 3);
    assertThat(simpleExpression.getVariables()).hasSize(1);
    assertThat(simpleExpression.getLimit()).isEqualTo(3);

    String result = simpleExpression.expand(Collections.singletonMap("var", "value"));
    assertThat(result).isEqualTo("val");
  }

  @Test
  void expand_withIterable() {
    SimpleExpression simpleExpression = new SimpleExpression("{list}");

    String result = simpleExpression.expand(
        Collections.singletonMap("list", Arrays.asList("red", "green", "blue")));
    assertThat(result).isEqualTo("red,green,blue");
  }

  @Test
  void expand_withIterableExplicit() {
    SimpleExpression simpleExpression = new SimpleExpression("{list}", -1, true);

    String result = simpleExpression.expand(
        Collections.singletonMap("list", Arrays.asList("red", "green", "blue")));
    assertThat(result).isEqualTo("red,green,blue");
  }

  @Test
  void expand_withComplexVariableName_andPctEncodedList() {
    SimpleExpression simpleExpression = new SimpleExpression("{list[]}");
    String result = simpleExpression.expand(
        Collections.singletonMap("list[]", Arrays.asList("red", "green", "blue")));
    assertThat(result).isEqualTo("red,green,blue");
  }

  @Test
  void expand_withValueAlreadyEncoded() {
    SimpleExpression simpleExpression = new SimpleExpression("{hello}");
    String result = simpleExpression.expand(Collections.singletonMap("hello", "Hello%20World%21"));
    assertThat(result).isEqualTo("Hello%20World%21");
  }

  @Test
  void expand_withMap() {
    SimpleExpression simpleExpression = new SimpleExpression("{keys}");
    Map<String, String> keys = new LinkedHashMap<>();
    keys.put("semi", ";");
    keys.put("dot", ".");
    keys.put("comma", ",");

    String result = simpleExpression.expand(Collections.singletonMap("keys", keys));
    assertThat(result).isEqualTo("semi,%3B,dot,.,comma,%2C");
  }

  @Test
  void expand_withMapExploded() {
    SimpleExpression simpleExpression = new SimpleExpression("{keys}", -1, true);
    Map<String, String> keys = new LinkedHashMap<>();
    keys.put("semi", ";");
    keys.put("dot", ".");
    keys.put("comma", ",");

    String result = simpleExpression.expand(Collections.singletonMap("keys", keys));
    assertThat(result).isEqualTo("semi=%3B,dot=.,comma=%2C");
  }
}