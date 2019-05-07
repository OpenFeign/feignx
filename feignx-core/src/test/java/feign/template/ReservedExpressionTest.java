package feign.template;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReservedExpressionTest extends ExpressionTest {

  @Override
  protected Expression getExpression(String variableSpecification, int limit) {
    Expression expression = new ReservedExpression(variableSpecification);
    expression.setLimit(limit);
    return expression;
  }

  @Override
  void expand_withMap() {
    Expression expression = this.getExpression("{keys}", -1);
    String result = expression.expand(Collections.singletonMap("keys", keys));
    assertThat(result).isEqualTo("semi,;,dot,.,comma,,");
  }

  @Override
  void expand_withMapExploded() {
    Expression expression = this.getExpression("{keys*}", -1);
    String result = expression.expand(Collections.singletonMap("keys", keys));
    assertThat(result).isEqualTo("semi=;,dot=.,comma=,");
  }

  @Override
  void expand_withMultipleVariables() {
    Expression expression = this.getExpression("{x,hello,y}", -1);
    assertThat(expression.getVariables()).hasSize(3);
    assertThat(expression.getLimit()).isEqualTo(-1);

    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("x", x);
    variables.put("hello", hello);
    variables.put("y", y);
    String result = expression.expand(variables);
    assertThat(result).isEqualTo(x + "," + helloReserved + "," + y);
  }

  @Override
  void expand_withBase() {
    Expression expression = this.getExpression("{base}", -1);
    String result = expression.expand(Collections.singletonMap("base", base));
    assertThat(result).isEqualTo("http://example.com/home/");
  }

  @Override
  void expand_withPath() {
    Expression expression = this.getExpression("{path}", -1);
    String result = expression.expand(Collections.singletonMap("path", path));
    assertThat(result).isEqualTo("/foo/bar");
  }
}
