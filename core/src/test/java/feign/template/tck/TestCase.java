/*
 * Copyright 2019 OpenFeign Contributors
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
