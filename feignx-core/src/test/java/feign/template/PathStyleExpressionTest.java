package feign.template;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class PathStyleExpressionTest extends StyleExpressionTest {

  @Override
  protected Expression getExpression(String variableSpecification, int limit) {
    Expression expression = new PathStyleExpression(variableSpecification);
    expression.setLimit(limit);
    return expression;
  }

  @Override
  @Test
  void expand_emptyAreKept() {
    Expression expression = this.getExpression("{x,empty}", -1);

    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("x", x);
    variables.put("empty", empty);
    String result = expression.expand(variables);
    assertThat(result).isEqualTo(expression.getPrefix() + "x=" + x
        + expression.getDelimiter() + "empty");
  }

  @Test
  void expand_withEmptyMixed() {
    Expression expression = this.getExpression("{x,empty,y}", -1);

    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("x", x);
    variables.put("empty", empty);
    variables.put("y", y);
    String result = expression.expand(variables);
    assertThat(result).isEqualTo(expression.getPrefix() + "x=" + x
        + expression.getDelimiter() + "empty"
        + expression.getDelimiter() + "y=" + y);
  }

  @Test
  void expand_withEmptyInList() {
    Expression expression = this.getExpression("{list}", -1);

    String result = expression.expand(Collections.singletonMap("list", Arrays.asList(x, empty, y)));
    assertThat(result).isEqualTo(expression.getPrefix() + "list=" + x
        + Expression.DEFAULT_DELIMITER
        + Expression.DEFAULT_DELIMITER + y);
  }

  @Test
  void expand_withEmptyInListExploded() {
    Expression expression = this.getExpression("{list*}", -1);
    String result = expression.expand(Collections.singletonMap("list", Arrays.asList(x, empty, y)));
    assertThat(result).isEqualTo(expression.getPrefix() + "list=" + x
        + expression.getDelimiter() + "list"
        + expression.getDelimiter() + "list=" + y);
  }

  @Test
  void expand_withEmptyValueInMap() {
    Expression expression = this.getExpression("{keys}", -1);

    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("x", x);
    variables.put("empty", empty);
    variables.put("y", y);
    String result = expression.expand(Collections.singletonMap("keys", variables));
    assertThat(result).isEqualTo(expression.getPrefix() + "keys=x"
        + Expression.DEFAULT_DELIMITER + x
        + Expression.DEFAULT_DELIMITER + "empty" + Expression.DEFAULT_DELIMITER
        + Expression.DEFAULT_DELIMITER + "y"
        + Expression.DEFAULT_DELIMITER + y);
  }

  @Test
  void expand_withEmptyValueInMapExploded() {
    Expression expression = this.getExpression("{keys*}", -1);

    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("x", x);
    variables.put("empty", empty);
    variables.put("y", y);
    String result = expression.expand(Collections.singletonMap("keys", variables));
    assertThat(result).isEqualTo(expression.getPrefix() + "x=" + x
        + expression.getDelimiter() + "empty="
        + expression.getDelimiter() + "y=" + y);
  }
}