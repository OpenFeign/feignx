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

package feign.http;

import feign.Client;
import java.util.Optional;

/**
 * An exception that occurred during a {@link Client} operation.
 */
public class HttpException extends RuntimeException {

  private HttpRequest request;
  private HttpResponse response;

  /**
   * Creates a new Http Exception.
   *
   * @param message for the exception.
   * @param cause of the exception.
   */
  public HttpException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Creates a new Http Exception.
   *
   * @param message for the exception.
   * @param cause of the exception.
   * @param request that was attempted.
   */
  public HttpException(String message, Throwable cause, HttpRequest request) {
    super(message, cause);
    this.request = request;
  }

  /**
   * Creates a new Http Exception.
   *
   * @param message for the exception.
   * @param cause of the exception.
   * @param request that was attempted.
   * @param response that was received.
   */
  public HttpException(String message, Throwable cause, HttpRequest request,
      HttpResponse response) {
    super(message, cause);
    this.request = request;
    this.response = response;
  }

  public Optional<HttpRequest> getRequest() {
    return Optional.ofNullable(this.request);
  }

  public Optional<HttpResponse> getResponse() {
    return Optional.ofNullable(this.response);
  }


}
