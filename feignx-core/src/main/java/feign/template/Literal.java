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

import feign.support.Assert;

/**
 * Chunk for static values.
 */
public class Literal implements Chunk {

  private final String value;

  /**
   * Creates a new Literal Chunk.
   *
   * @param value of the chunk.
   */
  public Literal(String value) {
    Assert.isNotEmpty(value, "value is required.");
    this.value = value;
  }

  /**
   * Chunk Value.
   *
   * @return the literal value.
   */
  @Override
  public String getValue() {
    return this.value;
  }
}
