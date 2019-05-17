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

package feign.support;

/**
 * String Utility Methods.
 */
public class StringUtils {

  /**
   * Determines if the provided String is not empty or {@literal null}.
   *
   * @param value to evaluate.
   * @return {@literal true} if the value is not empty or {@literal null}, {@literal false}
   *        otherwise.
   */
  public static boolean isNotEmpty(String value) {
    return value != null && !value.isEmpty();
  }

  /**
   * Determines if the provided String is empty or {@literal null}.
   *
   * @param value to evaluate.
   * @return {@literal true} if the value is empty or {@literal null}, {@literal false} otherwise.
   */
  public static boolean isEmpty(String value) {
    return value == null || value.isEmpty();
  }

}
