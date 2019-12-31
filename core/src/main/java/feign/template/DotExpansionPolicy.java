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

/**
 * Expression that uses the dot {@code .} character as a prefix and exploded delimiter, allowing
 * for expansion of domain names and other dot like values on a URI.
 */
class DotExpansionPolicy extends ExpansionPolicy {
  private static final DotExpansionPolicy instance = new DotExpansionPolicy();

  /**
   * Return a singleton instance of this Expansion Policy.
   *
   * @return expansion policy instance
   */
  public static DotExpansionPolicy getInstance() {
    return instance;
  }

  private DotExpansionPolicy() {
    super(".", ".", "", false, false);
  }
}
