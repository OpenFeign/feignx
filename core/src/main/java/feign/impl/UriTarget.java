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

package feign.impl;

import feign.http.RequestSpecification;
import feign.support.Assert;
import java.net.URI;

/**
 * Target that contains a base URI to apply to all requests.
 *
 * @param <T> type of the Target.
 */
public class UriTarget<T> extends AbstractTarget<T> {

  private URI uri;

  /**
   * Creates a new UriTarget.
   *
   * @param type of the Target to create.
   * @param uri base uri to apply to all requests.
   */
  public UriTarget(Class<T> type, String uri) {
    super(type);
    this.parseUri(uri);
  }

  /**
   * Creates a new UriTarget.
   *
   * @param type of the Target to create.
   * @param name of the Target.
   * @param uri base uri to apply to all requires.
   */
  public UriTarget(Class<T> type, String name, String uri) {
    super(type, name);
    this.parseUri(uri);
  }

  /**
   * Parse the provided URI.
   *
   * @param uri to parse.
   * @throws IllegalArgumentException if the uri is not valid or not absolute.
   */
  private void parseUri(String uri) {
    this.uri = URI.create(uri);
    Assert.isTrue(this.uri, URI::isAbsolute, "uri must be absolute.");
  }

  /**
   * Target the RequestSpecification by pre-pending any existing URI values with the
   * absolute URI in this target.  If the specification does not have a URI defined,
   * use the Target's URI.
   *
   * @param requestSpecification to target.
   */
  @Override
  public void apply(RequestSpecification requestSpecification) {
    URI targetUri = requestSpecification.uri()
        .map(current -> {
          /* the current uri must be relative to use this target */
          if (current.isAbsolute()) {
            throw new IllegalStateException("URIs must be relative when using a UriTarget.");
          }

          /* prepend this uri to the current uri */
          return URI.create(uri.toString() + current.toString());
        }).orElse(this.uri);

    /* update the uri on the specification */
    requestSpecification.uri(targetUri);
  }
}
