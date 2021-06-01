/*
 * Copyright 2019-2021 OpenFeign Contributors
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

package feign.exception;

/**
 * Base Exception for all internal errors.
 */
public class FeignException extends RuntimeException {

  private String method;

  /**
   * Creates a new Feign Exception.
   *
   * @param message for the exception.
   * @param method  name in which the exception occurred.
   */
  public FeignException(String message, String method) {
    super(message);
    this.method = method;
  }

  /**
   * Creates a new Feign Exception.
   *
   * @param message for the exception.
   * @param cause   of the exception.
   * @param method  name in which the exception occurred.
   */
  public FeignException(String message, Throwable cause, String method) {
    super(message, cause);
    this.method = method;
  }

  /**
   * Method name.
   *
   * @return the method name.
   */
  public String getMethod() {
    return method;
  }
}
