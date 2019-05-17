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

package feign;

import java.util.Collection;

/**
 * Represents a Header property for a Targeted Request.
 */
public interface Header {

  /**
   * Name of the Header.
   *
   * @return header name.
   */
  String name();

  /**
   * Values of the Header.
   *
   * @return header values.
   */
  Collection<String> values();

  /**
   * Add a Value to this Header.
   *
   * @param value to add.
   */
  void value(String value);
}
