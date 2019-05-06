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
    SimpleExpression simpleExpression = new SimpleExpression("{one}");
    assertThat(simpleExpression.getVariables()).hasSize(1);
    assertThat(simpleExpression.getLimit()).isEqualTo(-1);

    String result = simpleExpression.expand(Collections.singletonMap("one", "two"));
    assertThat(result).isEqualTo("two");
  }

  @Test
  void expand_withMultipleVariables() {
    SimpleExpression simpleExpression = new SimpleExpression("{one,two,three}");
    assertThat(simpleExpression.getVariables()).hasSize(3);
    assertThat(simpleExpression.getLimit()).isEqualTo(-1);

    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("one", "first");
    variables.put("two", "second");
    variables.put("three", "third");
    String result = simpleExpression.expand(variables);
    assertThat(result).isEqualTo("first,second,third");
  }

  @Test
  void expand_undefinedAreRemoved() {
    SimpleExpression simpleExpression = new SimpleExpression("{one,two,three}");

    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("one", "first");
    variables.put("two", "second");
    String result = simpleExpression.expand(variables);
    assertThat(result).isEqualTo("first,second");
  }

  @Test
  void expand_emptyAreKept() {
    SimpleExpression simpleExpression = new SimpleExpression("{one,two,three}");

    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("one", "first");
    variables.put("two", "second");
    variables.put("three", "");
    String result = simpleExpression.expand(variables);
    assertThat(result).isEqualTo("first,second,");
  }

  @Test
  void expand_withLimit() {
    SimpleExpression simpleExpression = new SimpleExpression("{one}", 5);
    assertThat(simpleExpression.getVariables()).hasSize(1);
    assertThat(simpleExpression.getLimit()).isEqualTo(5);

    String result = simpleExpression.expand(Collections.singletonMap("one", "limited"));
    assertThat(result).isEqualTo("limit");
  }

  @Test
  void expand_withIterable() {
    SimpleExpression simpleExpression = new SimpleExpression("{one}");

    String result = simpleExpression.expand(
        Collections.singletonMap("one", Arrays.asList("first", "second", "third")));
    assertThat(result).isEqualTo("first,second,third");
  }

  @Test
  void expand_withPctEncoding() {
    SimpleExpression simpleExpression = new SimpleExpression("{one}");

    String result = simpleExpression.expand(
        Collections.singletonMap("one", "Hello World"));
    assertThat(result).isEqualTo("Hello%20World");
  }
}