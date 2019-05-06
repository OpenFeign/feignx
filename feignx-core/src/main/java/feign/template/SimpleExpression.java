package feign.template;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SimpleExpression extends Expression {

  protected SimpleExpression(String variable) {
    super(variable);
  }

  public SimpleExpression(String variable, int limit) {
    super(variable, limit);
  }

  @Override
  protected String expandInternal(Object value) {
    if (Iterable.class.isAssignableFrom(value.getClass())) {
      List<String> values = new ArrayList<>();
      for (Object item : (Iterable) value) {
        values.add(item.toString());
      }
      return this.expandIterable(values);
    } else {
      return value.toString();
    }
  }

  /**
   * Expand the List of values.
   *
   * @param values to expand.
   * @return the expanded values.
   */
  protected String expandIterable(List<String> values) {
    return String.join(",", values);
  }

  private String expandValue(Object value) {
    StringBuilder expanded = new StringBuilder();

    return expanded.toString();
  }
}
