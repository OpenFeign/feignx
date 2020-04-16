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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * The Response model.
 */
public interface Response extends AutoCloseable {

  /**
   * Status Code.
   *
   * @return status code.
   */
  int status();

  /**
   * Status Reason.
   *
   * @return status reason.
   */
  String reason();

  /**
   * List of Headers provided in the Response.
   *
   * @return response headers.
   */
  List<Header> headers();

  /**
   * The length of the Response.
   *
   * @return content length.
   */
  int contentLength();

  /**
   * An Input Stream backed by the Response data.  It is the responsibility of the caller
   * to close this stream.
   *
   * @return a response data backed Input Stream.
   */
  InputStream body();

  /**
   * Reads the entire response and returns the data as a byte array.
   *
   * @return the Response data as a byte array.
   * @throws IOException if the response could not be read.
   */
  byte[] toByteArray() throws IOException;


}
