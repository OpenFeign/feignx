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

package feign.template;

/**
 * Manages the expansion of a given Expression.  Implementations are expected to provide a
 * default, no-argument constructor and be thread-safe, to encourage lazy initialization and reuse
 * between {@link feign.Target}s.
 */
public interface ExpressionExpander {

  /**
   * Expand the given expression, using the value provided.
   *
   * @param variable to expand.
   * @param value containing the variable values.
   * @return the expanded result, an empty string, or {@literal null}.
   */
  String expand(ExpressionVariable variable, Object value);

}
