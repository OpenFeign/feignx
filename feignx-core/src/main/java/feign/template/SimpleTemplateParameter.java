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

package feign.template;

import feign.support.Assert;
import java.util.Objects;

/**
 * Template Parameter implementation that acts as a simple value object.
 */
public class SimpleTemplateParameter implements TemplateParameter {

  private final String name;

  /**
   * Creates a new SimpleTemplateParameter.
   *
   * @param name of the parameter.
   * @throws IllegalArgumentException if the name is {@literal null} or empty.
   */
  public SimpleTemplateParameter(String name) {
    Assert.isNotEmpty(name, "name is required");
    this.name = name;
  }

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof SimpleTemplateParameter)) {
      return false;
    }
    SimpleTemplateParameter that = (SimpleTemplateParameter) obj;
    return name.toLowerCase().equals(that.name.toLowerCase());
  }

  @Override
  public int hashCode() {
    return Objects.hash(name.toLowerCase());
  }
}
