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

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

  @Test
  void create_withDefaultOptions() {
    HttpRequest request = new HttpRequest(
        URI.create("https://example.com"),
        HttpMethod.GET,
        Collections.emptyList(),
        null,
        null);
    assertThat(request.options()).isNotNull();
  }

  @Test
  void contentLength_isZero_ifNoBody() {
    HttpRequest request = new HttpRequest(
        URI.create("https://example.com"),
        HttpMethod.GET,
        Collections.emptyList(),
        null,
        null);
    assertThat(request.contentLength()).isZero();
  }

  @Test
  void request_toString() {
    HttpRequest request = new HttpRequest(
        URI.create("https://example.com"),
        HttpMethod.GET,
        Collections.emptyList(),
        null,
        null);
    assertThat(request.toString()).isNotEmpty();
  }
}