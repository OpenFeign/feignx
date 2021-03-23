/*
 * Copyright 2019-2021 OpenFeign Contributors
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

package feign;

import feign.support.Assert;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

/**
 * Definition of a Method Parameter on a Target Method.
 */
@ThreadSafe
@Immutable
public class TargetMethodParameterDefinition {

  private final String name;
  private final Integer index;
  private final String expanderClassName;

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Create a new {@link TargetMethodParameterDefinition}.
   *
   * @param name              of the parameter.
   * @param index             of the parameter in the method definition.
   * @param expanderClassName of the expander to use when resolving this parameter.
   */
  private TargetMethodParameterDefinition(String name, Integer index, String expanderClassName) {
    Assert.isNotEmpty(name, "name is required.");
    Assert.isNotNull(index, "argument index is required.");
    Assert.isTrue(index, idx -> idx >= 0, "argument index must be a positive number");
    this.name = name;
    this.index = index;
    this.expanderClassName = expanderClassName;
  }

  /**
   * The Name of the Parameter.
   *
   * @return parameter name.
   */
  public String getName() {
    return name;
  }

  /**
   * Argument Index of the Parameter.
   *
   * @return the argument index.
   */
  public Integer getIndex() {
    return index;
  }

  /**
   * Fully Qualified Class Name of the {@link feign.template.ExpressionExpander} to use when
   * expanding this parameter value.
   *
   * @return the fully qualified class name.
   */
  public String getExpanderClassName() {
    return expanderClassName;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof TargetMethodParameterDefinition)) {
      return false;
    }
    TargetMethodParameterDefinition that = (TargetMethodParameterDefinition) obj;
    return Objects.equals(name.toLowerCase(Locale.ROOT), that.name.toLowerCase(Locale.ROOT));
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ",
        TargetMethodParameterDefinition.class.getSimpleName() + "[", "]")
        .add("name='" + name + "'")
        .add("index=" + index)
        .add("expanderClassName='" + expanderClassName + "'")
        .toString();
  }

  /**
   * Builder for a Target Method Parameter Definition.
   */
  public static class Builder {

    private String name;
    private Integer index;
    private String expanderClassName;

    /**
     * Parameter Name.
     *
     * @param name of the parameter.
     * @return a builder instance for chaining.
     */
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Parameter Argument Index.
     *
     * @param index of the parameter in the method definition.
     * @return a builder instance for chaining.
     */
    public Builder index(Integer index) {
      this.index = index;
      return this;
    }

    /**
     * Expression Expander Fully Qualified Class name to use when resolving this parameter value.
     *
     * @param expanderClassName of the {@link feign.template.ExpressionExpander}
     * @return a builder instance for chaining.
     */
    public Builder expanderClassName(String expanderClassName) {
      this.expanderClassName = expanderClassName;
      return this;
    }

    /**
     * Create a new {@link TargetMethodParameterDefinition} from the builder properties.
     *
     * @return a new {@link TargetMethodParameterDefinition} instance.
     */
    public TargetMethodParameterDefinition build() {
      return new TargetMethodParameterDefinition(this.name, this.index, this.expanderClassName);
    }
  }
}
