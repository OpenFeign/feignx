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

import static feign.assertions.HttpHeaderAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class HttpHeaderTest {

  @Test
  void canCreate_withSingleValue() {
    HttpHeader httpHeader =
        new HttpHeader("Content-Length", Collections.singletonList("123456"));
    assertThat(httpHeader).hasName("Content-Length")
        .hasValues("123456");
  }

  @Test
  void cannotCreate_whenMultipleValuesAreNotAllowed() {
    assertThrows(IllegalStateException.class, () ->
        new HttpHeader("Content-Length", Arrays.asList("123456", "987654")));
  }

  @Test
  void canClear() {
    HttpHeader httpHeader =
        new HttpHeader("Content-Length", Collections.singletonList("123456"));
    httpHeader.clear();
    assertThat(httpHeader).hasNoValues();
  }

  @Test
  void canAddNewValue_WhenMultipleIsAllowed() {
    HttpHeader httpHeader =
        new HttpHeader("Accept", Collections.singletonList("application/json"));
    httpHeader.value("text/html");
    assertThat(httpHeader).hasValues("application/json", "text/html");
  }

  @Test
  void cannotAddNewValue_WhenMultipleIsNotAllowed() {
    HttpHeader httpHeader =
        new HttpHeader("Content-Type", Collections.singletonList("application/json"));
    assertThrows(IllegalStateException.class, () -> httpHeader.value("text/html"));
  }

  @Test
  void headersAreEqual_toItself() {
    HttpHeader httpHeader = new HttpHeader("X-Custom");
    assertThat(httpHeader).isEqualTo(httpHeader);
  }


  @Test
  void headersAreEqual_whenNamesMatch() {
    HttpHeader httpHeader = new HttpHeader("X-Custom");
    assertThat(httpHeader).isEqualTo(new HttpHeader("X-Custom"));
  }

  @Test
  void headersAreNotEqual_whenNotAHeader() {
    HttpHeader httpHeader = new HttpHeader("X-Custom");
    assertThat(httpHeader).isNotEqualTo("String");
    assertThat(httpHeader).isNotEqualTo(null);
  }

  @Test
  void headerToString_isNotNull() {
    HttpHeader httpHeader = new HttpHeader("X-Custom");
    assertThat(httpHeader.toString()).isNotEmpty();
  }

}