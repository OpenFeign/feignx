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
import feign.support.Assert;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Header implementation for Http Requests.
 */
public class HttpHeader implements Header {

  private static final List<String> HEADERS_WITH_MULTIPLE_VALUES =
      Arrays.asList("Accept", "Accept-Charset", "Accept-Encoding", "Accept-Language",
          "Allow", "Content-Language", "If-Match", "If-None_Match", "Range", "Upgrade",
          "Vary");
  private final String name;
  private final Set<String> values = new LinkedHashSet<>();
  private boolean multipleValuesAllowed;

  /**
   * Creates a new HttpHeader.
   *
   * @param name for the header.
   */
  public HttpHeader(String name) {
    this(name, Collections.emptyList());
  }

  /**
   * Creates a new HttpHeader.
   *
   * @param name of the header.
   * @param values for the header.
   * @throws IllegalArgumentException if either the name or values is {@literal null}
   */
  public HttpHeader(String name, Collection<String> values) {
    Assert.isNotNull(values, "values cannot be null.");
    this.name = name;
    this.multipleValuesAllowed = this.areMultipleValuesAllowed();

    /* create a deep copy of the values */
    if (!values.isEmpty()) {
      values.forEach(this::value);
    }
  }

  /**
   * Add a value to the Header.
   *
   * @param value to add.
   * @throws IllegalArgumentException if the value is {@literal null}
   * @throws IllegalStateException if the header name does not support multiple values.
   */
  public void value(String value) {
    Assert.isNotEmpty(value, "value is required.");
    if (!this.multipleValuesAllowed && !this.values.isEmpty()) {
      throw new IllegalStateException("Header " + this.name + " does not support multiple values.  "
          + "To change this value, please clear() it first.");
    }
    this.values.add(value);
  }

  /**
   * Clear the values for the header.
   */
  public void clear() {
    this.values.clear();
  }

  /**
   * The values for this header.
   *
   * @return a read-only collection containing the values.
   */
  public Collection<String> values() {
    return Collections.unmodifiableSet(this.values);
  }

  /**
   * Name of the header.
   *
   * @return header name.
   */
  public String name() {
    return this.name;
  }

  /**
   * Determines if multiple values are allowed for this header.
   *
   * @return {@literal true} if multiple values are allowed, {@literal false} otherwise.
   */
  private boolean areMultipleValuesAllowed() {
    return HEADERS_WITH_MULTIPLE_VALUES.stream()
        .anyMatch(header -> header.equalsIgnoreCase(name));
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof HttpHeader)) {
      return false;
    }
    HttpHeader that = (HttpHeader) obj;
    return name.toLowerCase().equals(that.name.toLowerCase());
  }

  @Override
  public int hashCode() {
    return Objects.hash(name.toLowerCase());
  }

  @Override
  public String toString() {
    return "HttpHeader: [" + "name='" + name + "'" + ", values=" + values + "]";
  }
}
