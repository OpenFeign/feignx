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
 * Expression that allows for characters in the Reserved to be included, without encoding.
 */
class ReservedExpansionPolicy extends ExpansionPolicy {
  private static final ReservedExpansionPolicy instance = new ReservedExpansionPolicy();

  /**
   * Return a singleton instance of this Expansion Policy.
   *
   * @return expansion policy instance
   */
  public static ReservedExpansionPolicy getInstance() {
    return instance;
  }

  private ReservedExpansionPolicy() {
    super("", ",", "", false, true);
  }
}
