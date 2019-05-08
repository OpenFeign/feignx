package feign.template;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ExpressionsTest {

  @Test
  void create_simpleExpression() {
    assertThat(Expressions.create("{simple}")).isInstanceOf(SimpleExpression.class);
  }

  @Test
  void create_simpleExpressionWithLimit() {
    Expression expression = Expressions.create("{simple:3}");
    assertThat(expression).isInstanceOf(SimpleExpression.class);
    assertThat(expression.getLimit()).isEqualTo(3);
  }

  @Test
  void create_simpleExpressionExploded() {
    Expression expression = Expressions.create("{simple*}");
    assertThat(expression).isInstanceOf(SimpleExpression.class);
    assertThat(expression.hasExplodeModifier()).isTrue();
  }

  @Test
  void create_simpleExpressionLimitAndExploded() {
    Expression expression = Expressions.create("{simple*:3}");
    assertThat(expression).isInstanceOf(SimpleExpression.class);
    assertThat(expression.hasExplodeModifier()).isTrue();
    assertThat(expression.getLimit()).isEqualTo(3);
  }

  @Test
  void create_expressionWithModifierAndLimitAndExploded() {
    Expression expression = Expressions.create("{+simple*:3}");
    assertThat(expression).isInstanceOf(ReservedExpression.class);
    assertThat(expression.hasExplodeModifier()).isTrue();
    assertThat(expression.getLimit()).isEqualTo(3);
  }

  @Test
  void create_reservedExpression() {
    assertThat(Expressions.create("{+simple}")).isInstanceOf(ReservedExpression.class);
  }

  @Test
  void create_dotExpression() {
    assertThat(Expressions.create("{.simple}")).isInstanceOf(DotExpression.class);
  }

  @Test
  void create_pathSegmentExpression() {
    assertThat(Expressions.create("{/simple}")).isInstanceOf(PathSegmentExpression.class);
  }

  @Test
  void create_pathStyleExpression() {
    assertThat(Expressions.create("{;simple}")).isInstanceOf(PathStyleExpression.class);
  }

  @Test
  void create_formStyleExpression() {
    assertThat(Expressions.create("{?simple}")).isInstanceOf(FormStyleExpression.class);
  }

  @Test
  void create_formContStyleExpression() {
    assertThat(Expressions.create("{&simple}")).isInstanceOf(FormContinuationStyleExpression.class);
  }

}