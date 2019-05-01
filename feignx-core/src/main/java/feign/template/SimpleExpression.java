package feign.template;

import java.util.ArrayList;
import java.util.List;

public class SimpleExpression extends Expression {

  protected SimpleExpression(String variable) {
    super(variable);
  }

  @Override
  protected String expandInternal(Object value) {
    /* collections of values are expanded differently */
    if (Iterable.class.isAssignableFrom(value.getClass())) {
      List<String> values = new ArrayList<>();
      for (Object item : (Iterable) value) {
        values.add(item.toString());
      }
      return this.expandIterable(values);
    }

    /* return the value, as a string */
    return value.toString();
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
}
