/*
 * Copyright 2019-2020 OpenFeign Contributors
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
import feign.template.ExpansionPolicy;

/**
 * Expression Expander that support List, Collection and other {@link Iterable} types.
 */
public class ListExpander extends MultiValueExpander {

  /* Singleton instantiation */
  private static final ListExpander instance = new ListExpander();

  public static ListExpander getInstance() {
    return instance;
  }

  @Override
  protected Iterable<?> getValues(Object value) {
    /* verify the value is an iterable type */
    Assert.isTrue(value,
        obj -> Iterable.class.isAssignableFrom(obj.getClass()),
        "Type " + value.getClass()
            + " is not supported by this expander. Values must be a List, Collection or "
            + "extend Iterable.");

    return (Iterable<?>) value;
  }

  /**
   * "Explodes" the value into a name,value pair per the specification.
   *
   * @param name of the value being expanded.
   * @param value to explode.
   * @param policy to apply when expanding the value.
   * @return the exploded expanded result.
   */
  @Override
  protected String explode(String name, Object value, ExpansionPolicy policy) {
    String expanded = value.toString();
    StringBuilder result = new StringBuilder();
    if (policy.isRequiredNamedParameters()) {
      result.append(this.encode(name, policy.isAllowReservedCharacters()));
      if (expanded.isEmpty()) {
        result.append(policy.getEmptySeparator());
      } else {
        result.append("=");
      }
    }

    result.append(this.encode(expanded, policy.isAllowReservedCharacters()));
    return result.toString();
  }

  /**
   * Expands the value.
   *
   * @param name of the value being expanded.
   * @param value to expand.
   * @param policy to apply when expanding the value.
   * @return the value provided expanded per the policy.
   */
  @Override
  protected String expand(String name, Object value, ExpansionPolicy policy) {
    return this.encode(value.toString(), policy.isAllowReservedCharacters());
  }
}
