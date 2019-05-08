package feign.template;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class PathSegmentExpressionTest extends SimpleExpressionTest {

  @Override
  protected Expression getExpression(String variableSpecification, int limit) {
    Expression expression = new PathSegmentExpression(variableSpecification);
    expression.setLimit(limit);
    return expression;
  }

  @Test
  void expand_withSlashPctEncoded() {
    Expression expression = this.getExpression("{who,dub}", -1);
    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("who", who);
    variables.put("dub", dub);
    String result = expression.expand(variables);
    assertThat(result).isEqualTo(expression.getPrefix() +
        who + expression.getDelimiter() + dubEncoded);
  }
}