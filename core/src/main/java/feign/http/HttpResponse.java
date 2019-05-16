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

import feign.Header;
import feign.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Http Response model.
 */
public final class HttpResponse implements Response {

  private int status;
  private String reason;
  private List<HttpHeader> headers = new ArrayList<>();
  private InputStream body;
  private int contentLength;
  private boolean closed = false;
  private byte[] content;

  /**
   * Creates a new HttpResponse Builder.
   *
   * @return a new builder instance.
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Creates a new HttpResponse.
   */
  HttpResponse() {
    super();
  }

  /**
   * The Response Status Code.
   *
   * @param status code.
   */
  private void status(int status) {
    this.status = status;
  }

  /**
   * Http Status Code.
   *
   * @return status code.
   */
  public int status() {
    return this.status;
  }

  /**
   * Set the reason from the Response.
   *
   * @param reason response reason.
   */
  private void reason(String reason) {
    this.reason = reason;
  }

  /**
   * HTTP Status Reason.
   *
   * @return status reason.
   */
  public String reason() {
    return this.reason;
  }

  /**
   * Set the Content Length.
   *
   * @param contentLength of the response.
   */
  private void contentLength(int contentLength) {
    this.contentLength = contentLength;
  }

  /**
   * The length of the Response.
   *
   * @return content length.
   */
  public int contentLength() {
    return this.contentLength;
  }

  /**
   * Set the Response body.
   *
   * @param body input stream.
   */
  private void body(InputStream body) {
    this.body = body;
  }

  /**
   * An Input Stream backed by the Response data.  It is the responsibility of the caller
   * to close this stream.
   *
   * @return a response data backed Input Stream.
   */
  public InputStream body() {
    if (this.content != null) {
      /* we have already read the entire contents, so return an array backed by the memory
       * buffer.
       */
      return new ByteArrayInputStream(this.content);
    }
    return this.body;
  }

  /**
   * List of Headers provided in the Response.
   *
   * @return response headers.
   */
  public List<Header> headers() {
    return Collections.unmodifiableList(this.headers);
  }

  /**
   * Add the Header to the Response.
   *
   * @param header to add.
   */
  private void addHeader(HttpHeader header) {
    this.headers.add(header);
  }

  /**
   * Reads the entire response and returns the data as a byte array.
   *
   * @return the Response data as a byte array.
   * @throws IOException if the response could not be read.
   */
  public byte[] toByteArray() throws IOException {

    if (this.content != null) {
      /* we have already read this stream */
      return this.content;
    }

    /* read the content into a memory buffer */
    try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
      byte[] input = new byte[this.contentLength];
      int len;
      while ((len = this.body.read(input, 0, input.length)) != -1) {
        buffer.write(input, 0, len);
      }
      buffer.flush();
      this.content = buffer.toByteArray();
    }
    return this.content;
  }

  /**
   * Close the Response.
   *
   * @throws Exception if the response cannot be closed.
   */
  @Override
  public void close() throws Exception {
    /* close our input stream if exists is not already closed */
    if (this.body != null) {
      this.body.close();
    }
    this.closed = true;
  }

  /**
   * If this Response has been closed.
   *
   * @return {@literal true} if close, {@literal false} otherwise.
   */
  public boolean isClosed() {
    return this.closed;
  }

  /**
   * Response Builder.
   */
  public static class Builder {

    private HttpResponse response = new HttpResponse();

    /**
     * Status code of the Response.
     *
     * @param status code.
     * @return the builder chain.
     */
    public Builder status(int status) {
      response.status(status);
      return this;
    }

    /**
     * Reason of the Response.
     *
     * @param reason description.
     * @return the builder chain.
     */
    public Builder reason(String reason) {
      response.reason(reason);
      return this;
    }

    /**
     * Size of the Response.
     *
     * @param contentLength with the size of the response.
     * @return the builder chain.
     */
    public Builder contentLength(int contentLength) {
      response.contentLength(contentLength);
      return this;
    }

    /**
     * Add the Header to the Response.
     *
     * @param header to add.
     * @return the builder chain.
     */
    public Builder addHeader(HttpHeader header) {
      response.addHeader(header);
      return this;
    }

    /**
     * The Request Body input stream.
     *
     * @param body input stream.
     * @return the builder chain.
     */
    public Builder body(InputStream body) {
      response.body(body);
      return this;
    }

    /**
     * Build the Response.
     *
     * @return a new Response.
     */
    public Response build() {
      return response;
    }

  }
}
