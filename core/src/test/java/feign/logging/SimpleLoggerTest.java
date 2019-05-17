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

package feign.logging;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import feign.Request;
import feign.Response;
import feign.http.HttpHeader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class SimpleLoggerTest {

  @Test
  void shouldNotLog_ifNotEnabled() {
    SimpleLogger simpleLogger = SimpleLogger.builder()
        .build();
    Request request = mock(Request.class);
    Response response = mock(Response.class);

    simpleLogger.logRequest("test", request);
    simpleLogger.logResponse("test", response);
    verifyZeroInteractions(request, response);
  }

  @Test
  void shouldLogRequestDetails_whenEnabled() {
    SimpleLogger simpleLogger = SimpleLogger.builder()
        .setEnabled(true)
        .build();

    Request request = mock(Request.class);
    simpleLogger.logRequest("test", request);
    verify(request).method();
    verify(request, times(0)).headers();
  }

  @Test
  void shouldLogRequestBody_whenEnabled() {
    SimpleLogger simpleLogger = SimpleLogger.builder()
        .setEnabled(true)
        .setRequestEnabled(true)
        .build();

    Request request = mock(Request.class);
    when(request.contentLength()).thenReturn(10);
    when(request.content()).thenReturn("content".getBytes(StandardCharsets.UTF_8));
    simpleLogger.logRequest("test", request);
    verify(request, times(1)).content();
    verify(request, times(0)).headers();
  }

  @Test
  void shouldNotLogRequestBody_whenBodyIsTooLarge() {
    SimpleLogger simpleLogger = SimpleLogger.builder()
        .setEnabled(true)
        .setRequestEnabled(true)
        .build();

    Request request = mock(Request.class);
    when(request.contentLength()).thenReturn(1024);
    simpleLogger.logRequest("test", request);
    verify(request, times(0)).content();
    verify(request, times(0)).headers();
  }

  @Test
  void shouldLogRequestHeaders_whenEnabled() {
    SimpleLogger simpleLogger = SimpleLogger.builder()
        .setEnabled(true)
        .setHeadersEnabled(true)
        .build();

    Request request = mock(Request.class);
    when(request.headers()).thenReturn(Collections.singletonList(new HttpHeader("stuff")));
    simpleLogger.logRequest("test", request);
    verify(request, times(0)).content();
    verify(request, times(1)).headers();
  }

  @Test
  void shouldLogResponseDetails_whenEnabled() {
    SimpleLogger simpleLogger = SimpleLogger.builder()
        .setEnabled(true)
        .build();

    Response response = mock(Response.class);
    simpleLogger.logResponse("test", response);
    verify(response).status();
    verify(response, times(0)).headers();
  }

  @Test
  void shouldLogResponseBody_whenEnabled() throws IOException {
    SimpleLogger simpleLogger = SimpleLogger.builder()
        .setEnabled(true)
        .setResponseEnabled(true)
        .build();

    Response response = mock(Response.class);
    when(response.contentLength()).thenReturn(8);
    when(response.toByteArray()).thenReturn("content".getBytes(StandardCharsets.UTF_8));
    simpleLogger.logResponse("test", response);
    verify(response, times(1)).toByteArray();
    verify(response, times(0)).headers();
  }

  @Test
  void shouldNotLogResponseBody_whenBodyIsTooLarge() throws IOException {
    SimpleLogger simpleLogger = SimpleLogger.builder()
        .setEnabled(true)
        .setResponseEnabled(true)
        .build();

    Response response = mock(Response.class);
    when(response.contentLength()).thenReturn(1024);
    simpleLogger.logResponse("test", response);
    verify(response, times(0)).toByteArray();
    verify(response, times(0)).headers();
  }

  @Test
  void shouldLogResponseHeaders_whenEnabled() throws IOException {
    SimpleLogger simpleLogger = SimpleLogger.builder()
        .setEnabled(true)
        .setHeadersEnabled(true)
        .build();

    Response response = mock(Response.class);
    when(response.headers()).thenReturn(Collections.singletonList(new HttpHeader("stuff")));
    simpleLogger.logResponse("test", response);
    verify(response, times(0)).toByteArray();
    verify(response, times(1)).headers();
  }
}