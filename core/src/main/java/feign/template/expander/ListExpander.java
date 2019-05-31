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
import feign.template.Expression;
import feign.template.ExpressionVariable;

/**
 * Expression Expander that support List, Collection and other {@link Iterable} types.
 */
public class ListExpander extends SimpleExpander {

  @Override
  public String expand(ExpressionVariable variable, Object value) {

    /* verify the value is an iterable type */
    Assert.isTrue(value,
        obj -> Iterable.class.isAssignableFrom(obj.getClass()),
        "Type " + value.getClass()
            + " is not supported by this expander. Values must be a List, Collection or "
            + "extend Iterable.");

    /* prefix limits are not allowed on lists */
    if (variable.getPrefix() > 0) {
      throw new IllegalStateException(
          "Prefix Limits are not allowed on map, composite, or list values");
    }

    /* expand the elements on the list */
    final Expression expression = variable.getExpression();
    final StringBuilder result = new StringBuilder();
    final StringBuilder builder = new StringBuilder();

    Iterable<?> valueCollection = (Iterable<?>) value;
    if (!valueCollection.iterator().hasNext()) {
      /* ignore empty lists */
      return null;
    }

    if (expression.expandNamedParameters() && !variable.isExploded()) {
      /* prepend the result with the variable name if named parameters are required. */
      result.append(this.encode(expression, variable.getName()))
          .append("=");
    }

    valueCollection.forEach(item -> {
      /* include the variable separator */
      if (builder.length() != 0) {
        builder.append(
            (variable.isExploded()) ? expression.getSeparator() : Expression.DEFAULT_SEPARATOR);
      }

      /* in the event that the variable is exploded and the modifier is one that
       * expands named parameters, treat the list as a map and create key,value pairs.
       */
      if (variable.isExploded() && expression.expandNamedParameters()) {
        builder.append(encode(expression, variable.getName()));
        builder.append("=");
      }

      /* append the expanded item value */
      builder.append(this.encode(expression, item.toString()));
    });

    /* append to the result */
    result.append(builder);
    return result.toString();
  }
}
