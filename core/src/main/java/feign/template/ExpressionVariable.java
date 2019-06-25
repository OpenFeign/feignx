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
import feign.support.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A variable specification present in a Uri Template Expression.
 */
public class ExpressionVariable {

  private static final Pattern VARIABLE_PATTERN =
      Pattern.compile("^([\\d\\w_*,.\\-\\[\\]%$]+)(:([\\d]*))?$");
  private static final String EXPLODE_MODIFIER = "*";
  private final Expression expression;
  private final String name;
  private boolean exploded;
  private int prefix;

  /**
   * Creates a new {@link ExpressionVariable}.
   *
   * @param expression this variable is part of.
   * @param variableSpec defining the variable specification.
   * @throws IllegalArgumentException if the expression or variableSpec are {@literal null}.
   *        or the variableSpec is not a valid specification.
   */
  ExpressionVariable(Expression expression, String variableSpec) {
    Assert.isNotNull(expression, "expression is required.");
    Assert.isNotEmpty(variableSpec, "variable specification is required.");

    /* store a reference to the expression this variable is part of */
    this.expression = expression;

    /* parse the variable spec */
    Matcher matcher = VARIABLE_PATTERN.matcher(variableSpec);
    if (!matcher.find()) {
      throw new IllegalArgumentException(
          "Variable: " + variableSpec + " is not a valid expression.");
    } else {
      /* name is the first group */
      String nameGroup = matcher.group(1);
      if (StringUtils.isNotEmpty(nameGroup)) {
        if (nameGroup.endsWith(EXPLODE_MODIFIER)) {
          /* this expression should be exploded */
          this.exploded = true;

          /* strip the modifier */
          nameGroup = nameGroup.substring(0, nameGroup.length() - 1);
        }
      }
      this.name = nameGroup;

      /* check for a prefix */
      String prefixGroup = matcher.group(3);
      if (StringUtils.isNotEmpty(prefixGroup)) {
        /* parse the prefix */
        try {
          this.prefix = Integer.valueOf(prefixGroup);
          if (prefix < 0 || prefix > 10000) {
            throw new IllegalArgumentException(
                "Prefix modifiers must be positive integers and less than 10,000");
          }
        } catch (NumberFormatException nfe) {
          throw new IllegalArgumentException(
              "Prefix modifiers must be positive integers and less than 10,000");
        }
      }
    }
  }

  /**
   * Creates a new Expression Variable.
   *
   * @param prefix of the variable.
   * @param name of the variable.
   * @param exploded flag if this variable should be expanded into it's exploded form.
   * @param expression this variable is part of.
   */
  public ExpressionVariable(int prefix, String name, boolean exploded, Expression expression) {
    this.prefix = prefix;
    this.name = name;
    this.exploded = exploded;
    this.expression = expression;
  }

  /**
   * Name of the variable.
   *
   * @return variable name.
   */
  public String getName() {
    return this.name;
  }

  /**
   * If this variable should be "exploded" when expanded.
   *
   * @return {@literal true} if the explode modifier is present, {@literal false} otherwise.
   */
  public boolean isExploded() {
    return this.exploded;
  }

  /**
   * Prefix limit for this variable.
   *
   * @return the prefix limit or {@code -1} if the entire value should be used.
   */
  public int getPrefix() {
    return this.prefix;
  }

  /**
   * {@link Expression} this variable belongs to.
   *
   * @return the {@link Expression} reference.
   */
  public Expression getExpression() {
    return this.expression;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder(this.name);
    if (this.prefix > 0) {
      builder.append(":")
          .append(this.prefix);
    }
    if (this.isExploded()) {
      builder.append(EXPLODE_MODIFIER);
    }
    return builder.toString();
  }
}
