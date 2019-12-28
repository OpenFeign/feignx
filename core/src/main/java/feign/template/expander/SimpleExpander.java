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

package feign.template.expander;

import feign.template.Expression;
import feign.template.ExpressionExpander;
import feign.template.ExpressionVariable;
import feign.template.UriUtils;

/**
 * Expression Expander that relies on the values {@link Object#toString()}.  This expander will
 * honor any prefix limits.
 */
public class SimpleExpander implements ExpressionExpander {

  /* Singleton instantiation */
  private static final SimpleExpander instance = new SimpleExpander();

  public static SimpleExpander getInstance() {
    return instance;
  }

  /**
   * Expand the given expression, using the value provided.
   *
   * @param variable to expand.
   * @param value containing the variable values.
   * @return the expanded result, an empty string, or {@literal null}.
   */
  @Override
  public String expand(ExpressionVariable variable, Object value) {
    if (value == null) {
      /* skip unresolved */
      return null;
    }

    /* expand the value */
    String result = value.toString();

    /* build the expanded expression */
    Expression expression = variable.getExpression();
    StringBuilder expanded = new StringBuilder();
    if (expression.requiredNamedParameters()) {
      expanded.append(this.encode(variable.getName(), expression.allowReservedCharacters()));
      if (result.isEmpty()) {
        expanded.append(expression.getEmptySeparator());
      } else {
        expanded.append("=");
      }
    }

    /* apply any limits */
    if (variable.getPrefix() > 0) {
      /* prefix the result */
      int limit = Math.min(variable.getPrefix(), result.length());
      result = result.substring(0, limit);
    }
    expanded.append(this.encode(result, expression.allowReservedCharacters()));

    /* return the pct-encoded result */
    return expanded.toString();
  }

  String encode(String value, boolean allowReservedCharacters) {
    return UriUtils.encode(value, allowReservedCharacters);
  }
}
