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
 * Manages expansion of an Expression.
 */
public class ExpansionPolicy {

  private final String start;
  private final String separator;
  private final String emptySeparator;
  private boolean requiredNamedParameters;
  private boolean allowReservedCharacters;

  /**
   * Creates a new Expansion Policy.
   *
   * @param start separator to use.
   * @param separator to use in-between variables.
   * @param emptySeparator to use when the expanded variable is empty.
   * @param requiredNamedParameters if the expansion should include the variable name.
   * @param allowReservedCharacters if the expansion should include reserved characters.
   */
  public ExpansionPolicy(String start, String separator, String emptySeparator,
      boolean requiredNamedParameters, boolean allowReservedCharacters) {
    this.start = start;
    this.separator = separator;
    this.emptySeparator = emptySeparator;
    this.requiredNamedParameters = requiredNamedParameters;
    this.allowReservedCharacters = allowReservedCharacters;
  }

  public String getStartSeparator() {
    return start;
  }

  public String getSeparator() {
    return separator;
  }

  public String getEmptySeparator() {
    return emptySeparator;
  }

  public boolean isRequiredNamedParameters() {
    return requiredNamedParameters;
  }

  public boolean isAllowReservedCharacters() {
    return allowReservedCharacters;
  }
}
