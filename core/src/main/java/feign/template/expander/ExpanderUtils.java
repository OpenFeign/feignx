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

import java.util.Map;

/**
 * Utilities related to variable expansion.
 */
class ExpanderUtils {

  /**
   * Determines if the provided Class is <em>simple</em>.  That is, something that is
   * not a core primitive, enum, annotation, array, String, Iterable, or Map.
   *
   * @param type to be evaluated.
   * @return {@literal true} if the type is considered <em>simple</em>, {@literal false} otherwise.
   */
  static boolean isSimpleType(Class<?> type) {
    return type.isAnnotation() || type.isArray() || type.isEnum() || type.isPrimitive()
        || String.class == type || Iterable.class.isAssignableFrom(type)
        || Map.class.isAssignableFrom(type);
  }

}
