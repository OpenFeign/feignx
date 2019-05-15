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

import feign.http.RequestSpecification;

/**
 * Represents a Target interface, containing the service definition.
 *
 * @param <T> type of the Target.
 */
public interface Target<T> {

  /**
   * Interface Type for this Target.
   *
   * @return the service definition type.
   */
  Class<T> type();

  /**
   * Short descriptive name for this Target.
   *
   * @return target name.
   */
  String name();

  /**
   * "Target"s the specification.
   *
   * @param requestSpecification to target.
   */
  void apply(RequestSpecification requestSpecification);
}
