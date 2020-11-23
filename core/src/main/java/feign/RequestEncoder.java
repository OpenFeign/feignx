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

package feign;

import feign.http.RequestSpecification;

/**
 * Prepares a {@link RequestEntity} for use.
 */
@FunctionalInterface
public interface RequestEncoder {

  /**
   * Encode the request content for use.  May return {@code null} if the entity should be
   * ignored.
   *
   * @param content to be encoded.
   * @param requestSpecification containing the current {@link RequestSpecification}.
   * @return a {@link RequestEntity} instance, or {@code null}.
   */
  RequestEntity apply(Object content, RequestSpecification requestSpecification);

}
