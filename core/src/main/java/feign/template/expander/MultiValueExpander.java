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

import feign.template.ExpansionPolicy;
import feign.template.Expression;
import feign.template.ExpressionVariable;

/**
 * Expression Expander that supports Collections, Lists, and Maps.
 */
public abstract class MultiValueExpander extends SimpleExpander {

  /**
   * Expand the value.
   *
   * @param name of the value being expanded.
   * @param value to expand.
   * @param policy to apply when expanding the value.
   * @return the expanded value.
   */
  protected abstract String expand(String name, Object value, ExpansionPolicy policy);

  /**
   * Expand the given expression, using the value provided.
   *
   * @param variable to expand.
   * @param values containing the variable values.
   * @return the expanded result, an empty string, or {@literal null}.
   */
  @Override
  public String expand(ExpressionVariable variable, Object values) {
    /* prefix limits are not allowed on lists */
    if (variable.getPrefix() > 0) {
      throw new IllegalStateException(
          "Prefix Limits are not allowed on map, composite, or list values");
    }

    final Expression expression = variable.getExpression();
    StringBuilder result = new StringBuilder();
    Iterable<?> collection = this.getValues(values);
    if (collection == null || !collection.iterator().hasNext()) {
      /* skip empty collections */
      return null;
    }

    for (Object value : collection) {
      if (value == null) {
        /* skip */
        continue;
      }

      String expanded;
      if (variable.isExploded()) {
        expanded = this.explode(variable.getName(), value, expression.getPolicy());
      } else {
        expanded = this.expand(variable.getName(), value, expression.getPolicy());
      }

      if (expanded == null) {
        continue;
      }

      if (result.length() != 0) {
        /* append the separator */
        if (variable.isExploded()) {
          result.append(expression.getSeparator());
        } else {
          result.append(",");
        }
      }
      result.append(expanded);
    }

    if (result.length() == 0) {
      return null;
    }

    if (!variable.isExploded()) {
      if (expression.requiredNamedParameters() && result.length() != 0) {
        /* start of the resolved expression, ensure that it starts with the variable name */
        result.insert(0,
            this.encode(variable.getName(), expression.allowReservedCharacters()) + "=");
      }
    }
    return result.toString();
  }

  /**
   * Returns the Iterable collection containing the values to expand.
   *
   * @param value being expanded.
   * @return an Iterable backed by the values.
   */
  protected abstract Iterable<?> getValues(Object value);

  /**
   * "Explodes" the value based on the specification.
   *
   * @param name of the value being expanded.
   * @param value to explode.
   * @param policy to apply when expanding the value.
   * @return the exploded expanded result.
   */
  protected abstract String explode(String name, Object value, ExpansionPolicy policy);



}
