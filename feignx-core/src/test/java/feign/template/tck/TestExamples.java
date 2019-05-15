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
