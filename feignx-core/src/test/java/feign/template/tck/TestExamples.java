package feign.template.tck;

import java.util.LinkedHashMap;
import java.util.Map;

public class TestExamples {

  private Map<String, TestGroup> testGroups;

  protected TestExamples() {
    super();
    this.testGroups = new LinkedHashMap<>();
  }

  public Map<String, TestGroup> getTestGroups() {
    return testGroups;
  }

  public void setTestGroups(
      Map<String, TestGroup> testGroups) {
    this.testGroups = testGroups;
  }
}
