package feign.template.tck;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TestGroup {

  private String level;
  private Map<String, Object> variables;

  @JsonProperty("testcases")
  private List<TestCase> testCases;

  protected TestGroup() {
    super();
    this.variables = new LinkedHashMap<>();
    this.testCases = new ArrayList<>();
  }

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public Map<String, Object> getVariables() {
    return variables;
  }

  public void setVariables(Map<String, Object> variables) {
    this.variables = variables;
  }

  public List<TestCase> getTestCases() {
    return testCases;
  }

  public void setTestCases(List<TestCase> testCases) {
    this.testCases = testCases;
  }
}
