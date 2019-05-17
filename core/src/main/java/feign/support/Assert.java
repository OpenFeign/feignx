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

import java.util.function.Predicate;

/**
 * Helper Class that provides useful Assertions.
 */
public final class Assert {

  /**
   * Ensures that the provided value is not {@literal null}.
   *
   * @param value to evaluate.
   * @param message to include in the thrown exception.
   * @throws IllegalArgumentException if the value is {@literal null}.
   */
  public static void isNotNull(Object value, String message) {
    if (value == null) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Ensures that the provided value is not empty.  Strings that are all whitespace are also
   * considered empty.
   *
   * @param value to evaluate.
   * @param message to include in the thrown exception.
   * @throws IllegalArgumentException if the value is {@literal null}, empty, or all whitespace.
   */
  public static void isNotEmpty(String value, String message) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Ensures that the value provided matches the supplied expression.
   *
   * @param value to evaluate.
   * @param expression to use to evaluate the value.
   * @param message to include in the thrown exception.
   * @param <T> type of the value.
   * @throws IllegalStateException if the value does not pass the expression.
   */
  public static <T> void isTrue(T value, Predicate<T> expression, String message) {
    if (!expression.test(value)) {
      throw new IllegalStateException(message);
    }
  }

  /**
   * Ensures that the value provided does not match the supplied expression.
   *
   * @param value to evaluate.
   * @param expression to use to evaluate the value.
   * @param message to include in the thrown exception.
   * @param <T> type of the value.
   * @throws IllegalStateException if the value does pass the expression.
   */
  public static <T> void isFalse(T value, Predicate<T> expression, String message) {
    isTrue(value, expression.negate(), message);
  }

}