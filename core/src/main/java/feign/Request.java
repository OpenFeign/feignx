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

import feign.http.HttpMethod;
import java.net.URI;
import java.util.List;

/**
 * Information about a Request to be made.
 */
public interface Request {

  /**
   * URI of the Target.
   *
   * @return request uri.
   */
  URI uri();

  /**
   * Content to be sent with the request.
   *
   * @return request content.
   */
  byte[] content();

  /**
   * Length of the Content to be sent.
   *
   * @return request content size.
   */
  int contentLength();

  /**
   * Http Method for this request.
   *
   * @return http method.
   */
  HttpMethod method();

  /**
   * Headers for the request.
   *
   * @return request headers.
   */
  List<Header> headers();

  /**
   * Options for this request.
   *
   * @return request options.
   */
  RequestOptions options();
}
