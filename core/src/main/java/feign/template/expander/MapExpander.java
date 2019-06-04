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

import feign.support.Assert;
import feign.support.StringUtils;
import feign.template.Expression;
import feign.template.ExpressionVariable;
import java.util.Map;

/**
 * Expression Expander that operates on associative arrays defined as {@link java.util.Map}s.
 */
public class MapExpander extends SimpleExpander {

  @Override
  public String expand(ExpressionVariable variable, Object value) {

    /* verify that the value is a map */
    Assert.isTrue(value,
        obj -> Map.class.isAssignableFrom(obj.getClass()),
        "Type " + value.getClass()
            + " is not supported by this expander.  Values must be a Map.");

    /* prefix limits are not allowed on maps */
    if (variable.getPrefix() > 0) {
      throw new IllegalStateException(
          "Prefix Limits are not allowed on map, composite, or list values");
    }

    /* expand the key,value pairs in the map */
    final Expression expression = variable.getExpression();
    final StringBuilder result = new StringBuilder();
    final StringBuilder builder = new StringBuilder();

    if (expression.expandNamedParameters() && !variable.isExploded()) {
      /* prepend the result with the variable name if named parameters are required. */
      result.append(this.encode(expression, variable.getName()))
          .append("=");
    }

    Map<?, ?> valueMap = (Map<?, ?>) value;
    if (valueMap.isEmpty()) {
      /* ignore empty maps */
      return null;
    }

    valueMap.forEach((key, val) -> {
      /* include the variable separator */
      if (builder.length() != 0) {
        builder.append(
            (variable.isExploded()) ? expression.getSeparator() : Expression.DEFAULT_SEPARATOR);
      }

      String encodedName = encode(expression, key.toString());
      String encodedValue = encode(expression, val.toString());

      /* append the variable name */
      builder.append(encodedName);

      if (StringUtils.isEmpty(encodedValue)) {
        /* special case: form style expressions omit the equals */
        if (!expression.isFormStyle()) {
          builder.append((variable.isExploded()) ? "=" : Expression.DEFAULT_SEPARATOR);
        }
      } else {
        /* append the rest of the key value pair */
        builder.append((variable.isExploded()) ? "=" : Expression.DEFAULT_SEPARATOR);
        builder.append(encodedValue);
      }
    });

    /* append to the result */
    result.append(builder);
    return result.toString();
  }
}
