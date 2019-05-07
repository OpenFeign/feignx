package feign.template;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

@SuppressWarnings("WeakerAccess")
public abstract class ExpressionTest {

  /* Constants for each example.  Taken from RFC 6570 */
  public static List<String> count = Arrays.asList("one", "two", "three");
  public static List<String> dom = Arrays.asList("example", "com");
  public static String dub = "me/too";
  public static String var = "value";
  public static String hello = "Hello World!";
  public static String helloEncoded = "Hello%20World%21";
  public static String helloReserved = "Hello%20World!";
  public static String half = "50%";
  public static String who = "fred";
  public static String base = "http://example.com/home/";
  public static String path = "/foo/bar";
  public static List<String> list = Arrays.asList("red", "green", "blue");
  public static Map<String, String> keys = new LinkedHashMap<String, String>() {{
    put("semi", ";");
    put("dot", ".");
    put("comma", ",");
  }};
  public static String v = "6";
  public static String x = "1024";
  public static String y = "768";
  public static String empty = "";
  public static Map<String, String> emptyKeys = Collections.emptyMap();
  public static final String undef = null;

  protected abstract Expression getExpression(String variableSpecification, int limit);

  @Test
  void expand_withSingleVariable() {
    Expression expression = this.getExpression("{var}", -1);
    assertThat(expression.getVariables()).hasSize(1);
    assertThat(expression.getLimit()).isEqualTo(-1);

    String result = expression.expand(Collections.singletonMap("var", var));
    assertThat(result).isEqualTo(var);
  }

  @Test
  void expand_withHalf() {
    Expression expression = this.getExpression("{half}", -1);
    String result = expression.expand(Collections.singletonMap("half", half));
    assertThat(result).isEqualTo("50%25");
  }

  @Test
  void expand_withBase() {
    Expression expression = this.getExpression("{base}", -1);
    String result = expression.expand(Collections.singletonMap("base", base));
    assertThat(result).isEqualTo("http%3A%2F%2Fexample.com%2Fhome%2F");
  }

  @Test
  void expand_withPath() {
    Expression expression = this.getExpression("{path}", -1);
    String result = expression.expand(Collections.singletonMap("path", path));
    assertThat(result).isEqualTo("%2Ffoo%2Fbar");
  }

  @Test
  void expand_withMultipleVariables() {
    Expression expression = this.getExpression("{x,hello,y}", -1);
    assertThat(expression.getVariables()).hasSize(3);
    assertThat(expression.getLimit()).isEqualTo(-1);

    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("x", x);
    variables.put("hello", hello);
    variables.put("y", y);
    String result = expression.expand(variables);
    assertThat(result).isEqualTo(x + "," + helloEncoded + "," + y);
  }

  @Test
  void expand_undefinedAreRemoved() {
    Expression expression = this.getExpression("{x,undef}", -1);

    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("x", x);
    String result = expression.expand(variables);
    assertThat(result).isEqualTo(x);
  }

  @Test
  void expand_emptyAreKept() {
    Expression expression =  this.getExpression("{x,empty}", -1);

    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("x", x);
    variables.put("empty", empty);
    String result = expression.expand(variables);
    assertThat(result).isEqualTo(x + ",");
  }

  @Test
  void expand_withLimit() {
    Expression expression = this.getExpression("{var}", 3);
    assertThat(expression.getVariables()).hasSize(1);
    assertThat(expression.getLimit()).isEqualTo(3);

    String result = expression.expand(Collections.singletonMap("var", var));
    assertThat(result).isEqualTo(var.substring(0, 3));
  }

  @Test
  void expand_withIterable() {
    Expression simpleExpression = this.getExpression("{list}", -1);

    String result = simpleExpression.expand(
        Collections.singletonMap("list", list));
    assertThat(result).isEqualTo("red,green,blue");
  }

  @Test
  void expand_withIterableExploded() {
    Expression expression = this.getExpression("{list*}", -1);

    String result = expression.expand(
        Collections.singletonMap("list", list));
    assertThat(result).isEqualTo("red,green,blue");
  }

  @Test
  void expand_withComplexVariableName_andPctEncodedList() {
    Expression expression = this.getExpression("{list[]}", -1);
    String result = expression.expand(
        Collections.singletonMap("list[]", list));
    assertThat(result).isEqualTo("red,green,blue");
  }

  @Test
  void expand_withValueAlreadyEncoded() {
    Expression expression = this.getExpression("{hello}", -1);
    String result = expression.expand(Collections.singletonMap("hello", helloEncoded));
    assertThat(result).isEqualTo(helloEncoded);
  }

  @Test
  void expand_withMap() {
    Expression expression = this.getExpression("{keys}", -1);
    String result = expression.expand(Collections.singletonMap("keys", keys));
    assertThat(result).isEqualTo("semi,%3B,dot,.,comma,%2C");
  }

  @Test
  void expand_withMapExploded() {
    Expression expression = this.getExpression("{keys*}", -1);
    String result = expression.expand(Collections.singletonMap("keys", keys));
    assertThat(result).isEqualTo("semi=%3B,dot=.,comma=%2C");
  }

}
