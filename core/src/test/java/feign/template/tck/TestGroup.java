/*
 * Copyright 2019-2020 OpenFeign Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package feign.template.tck;

import com.fasterxml.jackson.annotation.JsonProperty;
import feign.template.ExpressionExpander;
import feign.template.SimpleTemplateParameter;
import feign.template.TemplateParameter;
import feign.template.expander.ListExpander;
import feign.template.expander.MapExpander;
import feign.template.expander.SimpleExpander;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TestGroup {

  private String level;
  private Map<TemplateParameter, Object> variables;

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

  public Map<TemplateParameter, Object> getVariables() {
    return variables;
  }

  public void setVariables(Map<String, Object> variables) {
    for (Entry<String, Object> entry : variables.entrySet()) {
      Object value = entry.getValue();
      ExpressionExpander expander = new SimpleExpander();
      if (value instanceof Map) {
        expander = new MapExpander();
      } else if (value instanceof Iterable) {
        expander = new ListExpander();
      }

      this.variables.put(new SimpleTemplateParameter(entry.getKey(), expander), entry.getValue());
    }
  }

  public List<TestCase> getTestCases() {
    return testCases;
  }

  public void setTestCases(List<TestCase> testCases) {
    this.testCases = testCases;
  }
}
