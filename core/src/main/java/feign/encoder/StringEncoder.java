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

package feign.encoder;

import feign.RequestEncoder;
import feign.RequestEntity;
import feign.http.RequestSpecification;

/**
 * HttpRequest Encoder that encodes the request content as a String.  This implementation uses
 * the {@code toString()} method on the content object to encode the data.
 */
public class StringEncoder implements RequestEncoder {

  @Override
  public RequestEntity apply(Object content, RequestSpecification requestSpecification) {
    if (content != null) {
      return new StringRequestEntity(content.toString());
    }
    return null;
  }

}
