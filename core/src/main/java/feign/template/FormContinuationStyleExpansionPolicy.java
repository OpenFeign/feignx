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
 * Expression that expands values that represent the continuation of a Query String,
 * allowing for expansion of additional query parameters.
 */
class FormContinuationStyleExpansionPolicy extends ExpansionPolicy {
  private static final FormContinuationStyleExpansionPolicy instance =
      new FormContinuationStyleExpansionPolicy();

  /**
   * Return a singleton instance of this Expansion Policy.
   *
   * @return expansion policy instance
   */
  public static FormContinuationStyleExpansionPolicy getInstance() {
    return instance;
  }

  private FormContinuationStyleExpansionPolicy() {
    super("&", "&", "=", true, false);
  }
}
