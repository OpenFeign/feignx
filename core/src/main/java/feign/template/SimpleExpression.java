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

import java.util.List;

/**
 * Expression that expands object into a single value, pct-encoding all values not in the
 * unreserved set.
 */
class SimpleExpression extends AbstractExpression {

  SimpleExpression(List<String> variableSpecs) {
    super(variableSpecs);
  }

  SimpleExpression(List<String> variableSpecs, String operator) {
    super(variableSpecs, operator);
  }

  @Override
  public String getSeparator() {
    return Expression.DEFAULT_SEPARATOR;
  }

  @Override
  public boolean isCharacterAllowed(char character) {
    return UriUtils.isUnreserved(character);
  }

  @Override
  public boolean expandNamedParameters() {
    return false;
  }

  @Override
  public boolean isFormStyle() {
    return false;
  }
}
