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
import feign.template.ExpansionPolicy;
import java.util.Map;

/**
 * Expression Expander that operates on associative arrays defined as {@link java.util.Map}s.
 */
public class MapExpander extends ListExpander {

  /* Singleton instantiation */
  private static final MapExpander instance = new MapExpander();

  public static MapExpander getInstance() {
    return instance;
  }

  /**
   * Returns the underlying entry set for the map.
   *
   * @param value containing the map.
   * @return the underlying entry set for the map.
   */
  @Override
  protected Iterable<?> getValues(Object value) {
    /* verify that the value is a map */
    Assert.isTrue(value,
        obj -> Map.class.isAssignableFrom(obj.getClass()),
        "Type " + value.getClass()
            + " is not supported by this expander.  Values must be a Map.");

    return ((Map<?, ?>) value).entrySet();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String explode(String name, Object value, ExpansionPolicy policy) {
    Map.Entry<?, ?> entry = (Map.Entry<?, ?>) value;
    return this.expand(
        entry.getKey().toString(), entry.getValue().toString(), "=", policy);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String expand(String name, Object value, ExpansionPolicy policy) {
    Map.Entry<?, ?> entry = (Map.Entry<?, ?>) value;
    return this.expand(
        entry.getKey().toString(), entry.getValue().toString(), ",", policy);
  }

  /**
   * Expand a name,value pair.
   *
   * @param name of the pair.
   * @param value in the pair.
   * @param entrySeparator to use when joining the pair.
   * @param policy to use when expanding the value.
   * @return the expanded name,value pair.
   */
  private String expand(String name, String value, String entrySeparator, ExpansionPolicy policy) {
    StringBuilder result = new StringBuilder();

    result.append(this.encode(name, policy.isAllowReservedCharacters()));
    if (value.isEmpty()) {
      result.append(policy.getEmptySeparator());
    } else {
      result.append(entrySeparator);
    }
    result.append(this.encode(value, policy.isAllowReservedCharacters()));
    return result.toString();
  }

}
