/*
 * Copyright 2019-2021 OpenFeign Contributors
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Representation of an HTTP Request Header collection.
 */
public class HttpHeaders implements Iterable<Header> {

  private static final String CONTENT_TYPE = "Content-Type";
  private static final String CONTENT_LENGTH = "Content-Length";

  /* cached map to our headers for quick lookup */
  private final Map<String, Header> headers = new ConcurrentHashMap<>();

  /**
   * Create a new set of {@link HttpHeaders}.
   */
  public HttpHeaders() {
    super();
  }

  /**
   * Return a Stream backed by the underlying collection.
   *
   * @return Stream of HttpHeaders.
   */
  public Stream<Header> stream() {
    return this.headers.values().stream();
  }

  /**
   * Add the {@link HttpHeader} onto the Collection.
   *
   * @param header to add.
   */
  public void add(final Header header) {
    this.headers.compute(header.name(), (key, existing) -> {
      if (existing == null) {
        /* use the new header */
        return header;
      }

      /* merge this header with the other headers */
      header.values().forEach(existing::value);
      return existing;
    });
  }

  /**
   * Remove the {@link HttpHeader}.
   *
   * @param header to remove.
   */
  public void remove(final HttpHeader header) {
    this.headers.remove(header.name());
  }

  /**
   * Retrieve a HttpHeader, by name.
   *
   * @param name of the header.
   * @return the {@link HttpHeader} for this name, or {@code null} if not set.
   */
  public Header get(String name) {
    return this.headers.get(name);
  }

  /**
   * Set the Content-Type Header.
   *
   * @param contentType to use.
   */
  public void setContentType(final String contentType) {
    this.headers.compute(CONTENT_TYPE, (key, existing) -> {
      if (existing != null) {
        existing.clear();
        existing.value(contentType);
      } else {
        existing = new HttpHeader(CONTENT_TYPE, List.of(contentType));
      }
      return existing;
    });
  }

  /**
   * Set the Content-Length Header.
   *
   * @param contentLength of the request.
   */
  public void setContentLength(final int contentLength) {
    this.headers.compute(CONTENT_LENGTH, (key, existing) -> {
      if (existing != null) {
        existing.clear();
        existing.value(String.valueOf(contentLength));
      } else {
        existing = new HttpHeader(CONTENT_LENGTH, List.of(String.valueOf(contentLength)));
      }
      return existing;
    });
  }

  /**
   * The backing Collection.
   *
   * @return the collection of header values.
   */
  public Collection<Header> values() {
    return Set.copyOf(this.headers.values());
  }

  /**
   * Return an Iterator backed by the underlying collection.
   *
   * @return a new Iterator instance.
   */
  @Override
  public Iterator<Header> iterator() {
    return this.headers.values().iterator();
  }
}
