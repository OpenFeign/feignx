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

package feign.decoder;

import feign.Response;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Response Decoder that reads the response buffer and converts it to a
 * UTF-8 String.
 */
public class StringDecoder extends AbstractResponseDecoder {

  @SuppressWarnings("unchecked")
  @Override
  protected <T> T decodeInternal(Response response, Class<T> type) {
    if (String.class.equals(type)) {
      try {
        /* ready the body into a string */
        return (T) new String(response.toByteArray(), StandardCharsets.UTF_8);
      } catch (IOException ioe) {
        throw new IllegalStateException("Error occurred reading response: " + ioe, ioe);
      }
    }
    throw new IllegalArgumentException("Error occurred while decoding the Response, "
        + "type: " + type.getSimpleName() + " is not supported by this decoder.");
  }
}
