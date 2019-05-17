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

/**
 * Expression that expands object into a single value, pct-encoding all values not in the
 * unreserved set.
 */
public class SimpleExpression extends Expression {

  SimpleExpression(String variableSpecification) {
    super(variableSpecification);
  }

  SimpleExpression(String variableSpecification, boolean includeName) {
    super(variableSpecification, includeName);
  }

  @Override
  protected boolean isCharacterAllowed(char character) {
    /* only unreserved and our delimiter are allowed */
    return UriUtils.isUnreserved(character);
  }

  @Override
  protected String getDelimiter() {
    return Expression.DEFAULT_DELIMITER;
  }

  @Override
  protected String getPrefix() {
    return "";
  }
}
