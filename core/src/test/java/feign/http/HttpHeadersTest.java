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

import static org.assertj.core.api.Assertions.assertThat;

import feign.Header;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

  private HttpHeaders headers;

  @BeforeEach
  void setUp() {
    this.headers = new HttpHeaders();
  }

  @Test
  void canAdd() {
    HttpHeader header = new HttpHeader("simple", List.of("value"));
    this.headers.add(header);
    assertThat(this.headers).contains(header);
    assertThat(this.headers.values()).isNotEmpty().hasSize(1);
  }

  @Test
  void canRemove() {
    HttpHeader header = new HttpHeader("simple", List.of("value"));
    this.headers.add(header);
    assertThat(this.headers).contains(header);

    this.headers.remove(header);
    assertThat(this.headers).doesNotContain(header);
  }

  @Test
  void canAppend() {
    HttpHeader header = new HttpHeader("Accept", List.of("value"));
    this.headers.add(header);
    assertThat(this.headers).contains(header);

    this.headers.add(new HttpHeader("Accept", List.of("another")));
    assertThat(this.headers).hasSize(1);

    Header simple = this.headers.get("Accept");
    assertThat(simple).isNotNull();
    assertThat(simple.values()).hasSize(2)
        .contains("value", "another");
  }

  @Test
  void canIterateAndStream() {
    this.headers.add(new HttpHeader("simple", List.of("items")));
    this.headers.add(new HttpHeader("another", List.of("single")));

    assertThat(this.headers.stream()).isNotNull();
    assertThat(this.headers.iterator()).isNotNull();
    assertThat(this.headers).isNotEmpty();
    assertThat(this.headers.stream().count() == 2);
  }

  @Test
  void canSetContentType() {
    this.headers.setContentType("application/json");
    assertThat(this.headers).contains(
        new HttpHeader("Content-Type", List.of("application/json")));

    /* calling again should replace the content type */
    this.headers.setContentType("text/plain");
    assertThat(this.headers).contains(
        new HttpHeader("Content-Type", List.of("application/json")))
        .hasSize(1);
  }

  @Test
  void canSetContentLength() {
    this.headers.setContentLength(100);
    assertThat(this.headers).contains(
        new HttpHeader("Content-Length", List.of("100")))
        .hasSize(1);

    /* calling again should replace */
    this.headers.setContentLength(10);
    assertThat(this.headers).contains(
        new HttpHeader("Content-Length", List.of("10")))
        .hasSize(1);
  }

}