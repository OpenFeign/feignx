package feign.template.tck;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TestCase {

  private String expression;
  private List<String> results = new ArrayList<>();

  protected TestCase() {
    super();
  }

  @SuppressWarnings("unchecked")
  @JsonCreator
  protected TestCase(List<Object> values) {
    this.expression = (String) values.get(0);

    if (List.class.isAssignableFrom(values.get(1).getClass())) {
      this.results.addAll((List) values.get(1));
    } else if (Boolean.class.isAssignableFrom(values.get(1).getClass())) {
      this.results.add(((Boolean) values.get(1)).toString());
    } else {

      this.results.add((String) values.get(1));
    }
  }

  public String getExpression() {
    return expression;
  }


  public List<String> getResults() {
    return results;
  }

}
