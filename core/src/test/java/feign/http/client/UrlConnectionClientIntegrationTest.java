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

package feign.http.client;

import static feign.assertions.HttpResponseAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import feign.Request;
import feign.RequestOptions;
import feign.Response;
import feign.http.HttpException;
import feign.http.HttpHeader;
import feign.http.HttpMethod;
import feign.http.HttpRequest;
import feign.http.HttpResponse;
import java.io.BufferedInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;

class UrlConnectionClientIntegrationTest {

  private static final Random random = new Random();
  private static final byte[] data = new byte[2048];
  private static ClientAndServer mockServerClient;
  private UrlConnectionClient urlConnectionClient = new UrlConnectionClient();

  @BeforeAll
  static void startServer() {
    /* seed the data buffer */
    random.nextBytes(data);

    mockServerClient = ClientAndServer.startClientAndServer(1080);

    /* simple response for the index */
    mockServerClient.when(
        request()
            .withMethod("GET")
            .withPath("/")
    ).respond(
        response()
            .withStatusCode(200)
            .withBody("Hello!"));

    /* post with body */
    mockServerClient.when(
        request()
            .withMethod("POST")
            .withPath("/create")
            .withBody("content", StandardCharsets.UTF_8)
    ).respond(
        response()
            .withStatusCode(201));

    /* 4xx response */
    mockServerClient.when(
        request()
            .withMethod("POST")
            .withPath("/create")
            .withBody("incorrect", StandardCharsets.UTF_8)
    ).respond(
        response()
            .withStatusCode(400)
            .withReasonPhrase("Bad Request")
            .withBody("Incorrect Parameters"));

    /* 5xx response */
    mockServerClient.when(
        request()
            .withMethod("GET")
            .withPath("/status")

    ).respond(
        response()
            .withStatusCode(503)
            .withReasonPhrase("Service Unavailable")
            .withBody("Maintenance"));

    /* timeout response */
    mockServerClient.when(
        request()
            .withMethod("GET")
            .withPath("/delay")

    ).respond(
        response()
            .withStatusCode(502)
            .withDelay(TimeUnit.SECONDS, 2)
            .withReasonPhrase("Service Unavailable")
            .withBody("Maintenance"));

    /* large response */
    mockServerClient.when(
        request()
            .withMethod("GET")
            .withPath("/large")

    ).respond(
        response()
            .withStatusCode(502)
            .withDelay(TimeUnit.SECONDS, 2)
            .withReasonPhrase("Service Unavailable")
            .withBody(data));
  }

  @AfterAll
  static void stopServer() {
    mockServerClient.stop();
  }

  @Test
  void canSendRequest_withoutBody() throws Exception {
    HttpRequest request = new HttpRequest(
        URI.create("http://localhost:1080/"), HttpMethod.GET,
        null, RequestOptions.builder().build(), null);
    HttpResponse watched;
    try (Response response = this.urlConnectionClient.request(request)) {
      assertThat(response).isNotNull().isInstanceOf(HttpResponse.class);

      HttpResponse httpResponse = (HttpResponse) response;
      assertThat(httpResponse).hasStatus(200);

      String responseData = new String(httpResponse.toByteArray());
      assertThat(responseData).isEqualTo("Hello!");

      watched = httpResponse;
    }
    assertThat(watched.isClosed()).isTrue();
  }

  @Test
  void canSendRequest_withBody() throws Exception {


    HttpRequest request = new HttpRequest(
        URI.create("http://localhost:1080/create"), HttpMethod.POST,
        Collections.singletonList(new HttpHeader("Accept", Collections.singletonList("*/*"))),
        RequestOptions.builder().build(), "content".getBytes(StandardCharsets.UTF_8));
    try (Response response = this.urlConnectionClient.request(request)) {
      assertThat(response).isNotNull().isInstanceOf(HttpResponse.class);

      HttpResponse httpResponse = (HttpResponse) response;
      assertThat(httpResponse).hasStatus(201);
    }
  }

  @Test
  void canReadResponseStream_whenBodyIsLarge() throws Exception {
    HttpRequest request = new HttpRequest(
        URI.create("Http://localhost:1080/large"), HttpMethod.GET,
        Collections.emptyList(), RequestOptions.builder().build(), null);
    HttpResponse watched;
    try (Response response = this.urlConnectionClient.request(request)) {
      assertThat(response).isInstanceOf(HttpResponse.class);
      assertThat(response.contentLength()).isGreaterThanOrEqualTo(2048);

      try (BufferedInputStream bufferedInputStream = new BufferedInputStream(response.body())) {
        byte[] buffer = new byte[response.contentLength()];
        int read = bufferedInputStream.read(buffer, 0, response.contentLength());
        assertThat(read).isEqualTo(response.contentLength());
      }
      watched = (HttpResponse) response;
    }
    assertThat(watched.isClosed());
  }

  @Test
  void readErrorStream_whenStatusIs4xx() throws Exception {
    HttpRequest request = new HttpRequest(
        URI.create("http://localhost:1080/create"), HttpMethod.POST,
        null, RequestOptions.builder().build(), "incorrect".getBytes(StandardCharsets.UTF_8));
    try (Response response = this.urlConnectionClient.request(request)) {
      assertThat(response).isNotNull().isInstanceOf(HttpResponse.class);

      HttpResponse httpResponse = (HttpResponse) response;
      assertThat(httpResponse).hasStatus(400)
          .hasReason("Bad Request");
      String responseData = new String(httpResponse.toByteArray());
      assertThat(responseData).isEqualTo("Incorrect Parameters");
    }
  }

  @Test
  void readErrorStream_whenStatusIs5xx() throws Exception {
    HttpRequest request = new HttpRequest(
        URI.create("http://localhost:1080/status"), HttpMethod.GET,
        null, RequestOptions.builder().build(), null);
    try (Response response = this.urlConnectionClient.request(request)) {
      assertThat(response).isNotNull().isInstanceOf(HttpResponse.class);

      HttpResponse httpResponse = (HttpResponse) response;
      assertThat(httpResponse).hasStatus(503)
          .hasReason("Service Unavailable");
      String responseData = new String(httpResponse.toByteArray());
      assertThat(responseData).isEqualTo("Maintenance");
    }
  }

  @Test
  void throwIllegalArgument_onMalformedRequest() {
    HttpRequest request = new HttpRequest(
        URI.create("myscheme://localhost:1080/create"), HttpMethod.GET,
        Collections.emptyList(), RequestOptions.builder().build(), null);
    assertThrows(IllegalArgumentException.class, () -> this.urlConnectionClient.request(request));
  }

  @Test
  void throwIllegalArgument_onProtocolException() {
    HttpRequest request = new HttpRequest(
        URI.create("http://localhost:1080/create"), HttpMethod.CONNECT,
        Collections.emptyList(), RequestOptions.builder().build(), null);
    assertThrows(IllegalArgumentException.class, () -> this.urlConnectionClient.request(request));
  }

  @Test
  void throwHttpException_onIOExceptionResponse() {
    HttpRequest request = new HttpRequest(
        URI.create("http://localhost:1080/delay"), HttpMethod.GET,
        null,
        RequestOptions.builder()
            .setReadTimeout(1, TimeUnit.SECONDS)
            .build(),
        null);
    assertThrows(HttpException.class, () -> this.urlConnectionClient.request(request));
  }

  @Test
  void reject_nonHttpRequests() {
    assertThrows(IllegalArgumentException.class,
        () -> this.urlConnectionClient.request(mock(Request.class)));
  }

}