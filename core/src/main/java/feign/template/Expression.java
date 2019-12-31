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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents a <a href="https://tools.ietf.org/html/rfc6570#section-2.1">URI Template
 * Expression</a>.
 */
public final class Expression implements Chunk {

  private final List<ExpressionVariable> variables = new ArrayList<>();
  private final String operator;
  private final ExpansionPolicy policy;

  /**
   * Creates a new Expression.
   *
   * @param variables contained within the expression.
   * @param operator for the expression, may be {@literal null}.
   * @param policy that manages the expansion of this expression.
   */
  public Expression(List<String> variables, String operator,
      ExpansionPolicy policy) {
    Assert.isNotEmpty(variables, "variables are required.");

    /* create a copy of the provided values to prevent any liveliness issues */
    List<String> specifications = Collections.unmodifiableList(new ArrayList<>(variables));
    for (String specification : specifications) {
      this.variables.add(new ExpressionVariable(this, specification));
    }
    this.operator = operator;
    this.policy = policy;
  }

  public ExpansionPolicy getPolicy() {
    return this.policy;
  }

  /**
   * Variables contained within this Expression.
   *
   * @return a collection of {@link ExpressionVariable}s
   */
  public List<ExpressionVariable> getVariables() {
    return Collections.unmodifiableList(this.variables);
  }

  /**
   * The Operator that may be present on the expression.  Operators modify the expansion process.
   *
   * @return the operator value, if present.
   */
  public Optional<String> getOperator() {
    return Optional.ofNullable(this.operator);
  }

  /**
   * Separator to use when joining associative array, list, and multiple variable results.
   *
   * @return variable result separator.
   */
  public String getSeparator() {
    return this.policy.getSeparator();
  }

  /**
   * Separator to use if the {@link ExpressionVariable} expands into an empty string and a
   * separator is required.
   *
   * @return separator to use on an empty value.
   */
  public String getEmptySeparator() {
    return this.policy.getEmptySeparator();
  }

  /**
   * Separator used on the first valid expanded {@link ExpressionVariable}.
   *
   * @return the first separator
   */
  public String getStartSeparator() {
    return this.policy.getStartSeparator();
  }

  /**
   * Determines if this expression allows reserved characters.
   *
   * @return if reserved characters should be encoded.
   */
  public boolean allowReservedCharacters() {
    return this.policy.isAllowReservedCharacters();
  }

  /**
   * Determines if this expression should expand into named parameters.  This support is limited
   * to the various "style" based expansion operators.
   *
   * @return if the expanded values should include named parameters.
   */
  public boolean requiredNamedParameters() {
    return this.policy.isRequiredNamedParameters();
  }

  /**
   * Return the expression's parsed value.
   *
   * @return the expressions value.
   */
  @Override
  public String getValue() {
    StringBuilder builder = new StringBuilder("{");
    this.getOperator().ifPresent(builder::append);
    builder.append(this.variables.stream()
        .map(ExpressionVariable::toString)
        .collect(Collectors.joining(",")));
    builder.append("}");
    return builder.toString();
  }
}
