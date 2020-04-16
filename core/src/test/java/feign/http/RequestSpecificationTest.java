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

import static feign.assertions.HttpRequestAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import feign.Header;
import feign.Request;
import feign.RequestOptions;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class RequestSpecificationTest {

  @Test
  void canBuildRequest() {
    RequestSpecification requestSpecification = new RequestSpecification();
    URI uri = URI.create("https://www.example.com");
    Request request = requestSpecification.uri(uri)
        .method(HttpMethod.GET)
        .header("Accept", "*/*")
        .parameter("name", "value")
        .followRedirects(true)
        .readTimeout(2, TimeUnit.SECONDS)
        .connectTimeout(1, TimeUnit.SECONDS)
        .content("data".getBytes(StandardCharsets.UTF_8))
        .build();
    assertThat(request).isInstanceOf(HttpRequest.class);

    HttpRequest httpRequest = (HttpRequest) request;
    URI result = URI.create("https://www.example.com?name=value");
    RequestOptions options = RequestOptions.builder()
        .setConnectTimeout(1, TimeUnit.SECONDS)
        .setReadTimeout(2, TimeUnit.SECONDS)
        .build();
    assertThat(requestSpecification.method()).isEqualByComparingTo(HttpMethod.GET);
    assertThat(httpRequest).hasUri(result)
        .hasMethod(HttpMethod.GET)
        .hasHeaders(Collections.singletonList(new HttpHeader("Accept", Collections.singletonList("value"))))
        .hasOptions(options)
        .hasContent("data".getBytes(StandardCharsets.UTF_8));
  }

  @Test
  void additionalParameters_areAppendedToQuery() {
    RequestSpecification requestSpecification = new RequestSpecification();
    URI uri = URI.create("https://www.example.com?name=value");
    Request request = requestSpecification.uri(uri)
        .method(HttpMethod.GET)
        .parameter("filter", "name")
        .parameter("filter", "address")
        .parameter("sort", "desc")
        .build();
    assertThat(request).isInstanceOf(HttpRequest.class);

    HttpRequest httpRequest = (HttpRequest) request;
    URI result = URI.create(
        "https://www.example.com?name=value&filter=name&filter=address&sort=desc");
    assertThat(httpRequest).hasUri(result);
  }

  @Test
  void additionalParameters_areAppendedToQueryEncoded() {
    RequestSpecification requestSpecification = new RequestSpecification();
    URI uri = URI.create("https://www.example.com?name=value");
    Request request = requestSpecification.uri(uri)
        .method(HttpMethod.GET)
        .parameter("filter", "&?name")
        .parameter("filter", "address")
        .parameter("sort", "desc")
        .build();
    assertThat(request).isInstanceOf(HttpRequest.class);

    HttpRequest httpRequest = (HttpRequest) request;
    URI result = URI.create(
        "https://www.example.com?name=value&filter=%26%3Fname&filter=address&sort=desc");
    assertThat(httpRequest).hasUri(result);
  }

  @Test
  void additionalParameters_areAppendedToQueryWithFragmentAndPath() {
    RequestSpecification requestSpecification = new RequestSpecification();
    URI uri = URI.create("https://www.example.com/resources/items?name=value#topics");
    Request request = requestSpecification.uri(uri)
        .method(HttpMethod.GET)
        .parameter("filter", "name")
        .build();
    assertThat(request).isInstanceOf(HttpRequest.class);

    HttpRequest httpRequest = (HttpRequest) request;
    URI result = URI.create(
        "https://www.example.com/resources/items?name=value&filter=name#topics");
    assertThat(httpRequest).hasUri(result);
  }

  @Test
  void additionalParametersWithPort_areAppendedToQuery() {
    RequestSpecification requestSpecification = new RequestSpecification();
    URI uri = URI.create("https://www.example.com:8080?name=value");
    Request request = requestSpecification.uri(uri)
        .method(HttpMethod.GET)
        .parameter("filter", "name")
        .parameter("filter", "address")
        .parameter("sort", "desc")
        .build();
    assertThat(request).isInstanceOf(HttpRequest.class);

    HttpRequest httpRequest = (HttpRequest) request;
    URI result = URI.create(
        "https://www.example.com:8080?name=value&filter=name&filter=address&sort=desc");
    assertThat(httpRequest).hasUri(result);
  }

  @Test
  void additionalParametersWithUserInfo_areAppendedToQuery() {
    RequestSpecification requestSpecification = new RequestSpecification();
    URI uri = URI.create("https://user:password@www.example.com:8080?name=value");
    Request request = requestSpecification.uri(uri)
        .method(HttpMethod.GET)
        .parameter("filter", "name")
        .parameter("filter", "address")
        .parameter("sort", "desc")
        .build();
    assertThat(request).isInstanceOf(HttpRequest.class);

    HttpRequest httpRequest = (HttpRequest) request;
    URI result = URI.create(
        "https://user:password@www.example.com:8080?name=value&filter=name&filter=address"
            + "&sort=desc");
    assertThat(httpRequest).hasUri(result);
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  @Test
  void appendHeaderValue() {
    RequestSpecification requestSpecification = new RequestSpecification();
    Request request = requestSpecification.uri(URI.create("https://www.example.com"))
        .header("Accept", "application/json")
        .header("Accept", "text/html")
        .method(HttpMethod.GET)
        .build();
    assertThat(request.headers()).isNotEmpty();
    assertThat(requestSpecification.header("Accept")).isNotEmpty();
    Optional<Header> header = requestSpecification.header("Accept");
    assertThat(header.get().values()).isNotEmpty().hasSize(2);

  }
}