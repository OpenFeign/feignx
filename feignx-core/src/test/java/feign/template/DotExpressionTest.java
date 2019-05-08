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