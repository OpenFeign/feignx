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

/**
 * A component that can parse a given Response and "decode" the Response body into the
 * type desired.
 */
@FunctionalInterface
public interface ResponseDecoder {

  /**
   * Decode the Response Body into the desired type.
   *
   * @param response to decode.
   * @param type instance desired.
   * @param <T> desired type.
   * @return an instance of the desired type.
   * @throws feign.exception.FeignException if the response could not be decoded.
   */
  <T> T decode(Response response, Class<T> type);
}
