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

package feign.impl;

import feign.http.RequestSpecification;
import java.net.URI;
import java.util.function.Consumer;

/**
 * Consumer for handling absolute base URI targets.
 */
public class AbsoluteUriTarget implements Consumer<RequestSpecification> {

  private final URI uri;

  public AbsoluteUriTarget(String uri) {
    this.uri = URI.create(uri);
  }

  public AbsoluteUriTarget(URI uri) {
    this.uri = uri;
  }

  @Override
  public void accept(RequestSpecification specification) {
    specification.uri(specification.uri()
        .map(current -> {
          /* the current uri must be relative to use this target */
          if (current.isAbsolute()) {
            throw new IllegalStateException("URIs must be relative when using a UriTarget.");
          }

          /* prepend this uri to the current uri */
          return URI.create(uri.toString() + current);
        }).orElse(uri));
  }
}
