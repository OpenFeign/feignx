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

import java.util.Collection;
import java.util.Optional;

/**
 * Represents a <a href="https://tools.ietf.org/html/rfc6570#section-2.1">URI Template
 * Expression</a>.
 */
public interface Expression extends Chunk {

  String DEFAULT_SEPARATOR = ",";

  /**
   * Variables contained within this Expression.
   *
   * @return a collection of {@link ExpressionVariable}s
   */
  Collection<ExpressionVariable> getVariables();

  /**
   * The Operator that may be present on the expression.  Operators modify the expansion process.
   *
   * @return the operator value, if present.
   */
  Optional<String> getOperator();

  /**
   * Separator to use when joining associative array, list, and multiple variable results.
   *
   * @return variable result separator.
   */
  String getSeparator();

  /**
   * Determines if the provided character is allowed in this expressions expanded result without
   * pct-encoding.
   *
   * @param character to evaluate.
   * @return {@literal true} if the character is allowed without encoding, {@literal false} if the
   *        value must be encoded.
   */
  boolean isCharacterAllowed(char character);

  /**
   * Determines if this expression should expand into named parameters.  This support is limited
   * to the various "style" based expansion operators.
   *
   * @return if the expanded values should include named parameters.
   */
  boolean expandNamedParameters();

  /**
   * Determines if this expression should expand into application/www-form-urlencoded
   * style.
   *
   * @return if the expanded value should be in application/www-form-urlencoded style.
   */
  boolean isFormStyle();

}
