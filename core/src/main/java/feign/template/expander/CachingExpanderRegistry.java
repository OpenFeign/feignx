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

import feign.template.ExpanderRegistry;
import feign.template.ExpressionExpander;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Expander Registry that caches {@link ExpressionExpander} instances and reuses them.
 */
public class CachingExpanderRegistry implements ExpanderRegistry {

  private final Map<Class<?>, ExpressionExpander> expanderMapCache = new ConcurrentHashMap<>();

  /**
   * Retrieves the {@link ExpressionExpander} registered for the specified type.  In the event
   * that there is no expander registered, a new expander will be created and returned.
   *
   * @param parameterType to be expanded.
   * @return an {@link ExpressionExpander} instance.
   */
  @Override
  public ExpressionExpander getExpanderByType(Class<?> parameterType) {
    /* uses singleton instances for our internal expander instances */
    return this.expanderMapCache.computeIfAbsent(parameterType,
        type -> {
          if (Iterable.class.isAssignableFrom(type)) {
            return ListExpander.getInstance();
          } else if (Map.class.isAssignableFrom(type)) {
            return MapExpander.getInstance();
          } else if (ExpanderUtils.isSimpleType(type)) {
            return SimpleExpander.getInstance();
          } else {
            return BeanExpander.getInstance(this);
          }
        });
  }

  /**
   * Retrieve the {@link ExpressionExpander} instance for the specified custom
   * {@link ExpressionExpander} type specified.  If no instance exists, a new instance will be
   * created and cached.
   *
   * @param expanderClass to retrieve.
   * @return an instance of the provided expander.
   */
  @Override
  public ExpressionExpander getExpander(Class<? extends ExpressionExpander> expanderClass) {
    return this.expanderMapCache.computeIfAbsent(expanderClass,
        type -> {
          try {
            return (ExpressionExpander) type.getDeclaredConstructor().newInstance();
          } catch (Exception ex) {
            throw new IllegalStateException("Error occurred creating custom expander instance "
                + "for type " + type.getSimpleName() + ".  "
                + "Could not create instance." + ex.getMessage(), ex);
          }
        });
  }


}
