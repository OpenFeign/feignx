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

package feign.http;

import feign.Header;
import feign.Request;
import feign.RequestEntity;
import feign.RequestOptions;
import feign.support.StringUtils;
import java.net.URI;
import java.util.Collection;

/**
 * Http Request Model.
 */
public final class HttpRequest implements Request {

  private URI uri;
  private HttpMethod method;
  private final HttpHeaders headers = new HttpHeaders();
  private RequestOptions options;
  private RequestEntity content;

  /**
   * Creates a new empty HttpRequest.
   */
  HttpRequest() {
    super();
  }

  /**
   * Creates a new HttpRequest.
   *
   * @param uri for the request.
   * @param method for the request.
   * @param headers to include on the request.
   * @param options for the request.
   * @param content to include in the request.
   */
  public HttpRequest(URI uri, HttpMethod method, Collection<Header> headers, RequestOptions options,
      RequestEntity content) {
    this.uri = uri;
    this.method = method;
    if (headers != null) {
      headers.forEach(this.headers::add);
    }
    this.options = (options == null) ? RequestOptions.builder().build() : options;
    this.content = content;
    this.updateContentRelatedHeaders();
  }

  /**
   * URI of the Request.
   *
   * @return request uri.
   */
  @Override
  public URI uri() {
    return this.uri;
  }

  /**
   * Request Content.  Can be {@literal null}.
   *
   * @return the request content, if any.
   */
  @Override
  public byte[] content() {
    return (this.content != null) ? this.content.getData() : null;
  }

  /**
   * Request Content size.
   *
   * @return content size.
   */
  @Override
  public int contentLength() {
    return (this.content != null) ? this.content.getContentLength() : 0;
  }

  /**
   * Request Content type.
   *
   * @return content type, may be {@code null}
   */
  @Override
  public String contentType() {
    return (this.content != null) ? this.content.getContentType() : null;
  }

  /**
   * Http Method for the Request.
   *
   * @return the http method.
   */
  @Override
  public HttpMethod method() {
    return this.method;
  }

  /**
   * Headers for the Request.
   *
   * @return an array of Request Headers.
   */
  @Override
  public Collection<Header> headers() {
    return this.headers.values();
  }

  /**
   * Options for the Request.
   *
   * @return request options.
   */
  @Override
  public RequestOptions options() {
    return this.options;
  }

  @Override
  public String toString() {
    return "HttpRequest [" + "uri=" + uri
        + ", method=" + method
        + ", headers=" + headers
        + ", options=" + options
        + "]";
  }

  /**
   * Apply any content related headers when a {@link RequestEntity} is present.
   */
  private void updateContentRelatedHeaders() {
    if (this.content != null) {
      if (this.content.getContentLength() != 0) {
        this.headers.setContentLength(this.content.getContentLength());
      }

      if (StringUtils.isNotEmpty(this.content.getContentType())) {
        String contentType = this.content.getContentType();
        if (this.content.getCharset().isPresent()) {
          contentType += "; charset=" + this.content.getCharset().get().name();
        }
        this.headers.setContentType(contentType);
      }
    }
  }
}
