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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import feign.Client;
import feign.Logger;
import feign.Request;
import feign.RequestEncoder;
import feign.RequestInterceptor;
import feign.Response;
import feign.ResponseDecoder;
import feign.TargetMethodDefinition;
import feign.TargetMethodHandler;
import feign.ExceptionHandler;
import feign.http.HttpMethod;
import feign.http.RequestSpecification;
import feign.template.SimpleTemplateParameter;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AbstractTargetMethodHandlerTest {

  private TargetMethodDefinition targetMethodDefinition;

  @Mock
  private RequestEncoder encoder;

  @Mock
  private Client client;

  @Mock
  private ResponseDecoder decoder;

  @Mock
  private ExceptionHandler exceptionHandler;

  @Mock
  private RequestInterceptor interceptor;

  @Mock
  private Response response;

  @Mock
  private Logger logger;

  private Executor executor = Executors.newSingleThreadExecutor();

  @BeforeEach
  void setUp() {
    this.targetMethodDefinition = new TargetMethodDefinition(
        new UriTarget<>(TestInterface.class, "https://www.example.com"));

  }

  @Test
  void interceptors_areApplied_ifPresent() throws Throwable {
    when(this.client.request(any(Request.class))).thenReturn(this.response);
    this.targetMethodDefinition.returnType(String.class)
        .uri("/resources/{name}")
        .method(HttpMethod.GET)
        .templateParameter(0, new SimpleTemplateParameter("name"))
        .body(1);

    TargetMethodHandler targetMethodHandler = new BlockingTargetMethodHandler(
        this.targetMethodDefinition,
        this.encoder,
        Collections.singletonList(this.interceptor),
        this.client,
        this.decoder,
        this.exceptionHandler,
        this.executor,
        this.logger);

    targetMethodHandler.execute(Arrays.array("name", "body"));
    verify(encoder, times(1)).apply(any(), any(RequestSpecification.class));
    verify(client, times(1)).request(any(Request.class));
    verify(decoder, times(1)).decode(any(Response.class), eq(String.class));
    verify(interceptor, times(1)).accept(any(RequestSpecification.class));
    verifyZeroInteractions(this.exceptionHandler);
  }

  @Test
  void interceptors_areNotApplied_ifNotPresent() throws Throwable {
    when(this.client.request(any(Request.class))).thenReturn(this.response);
    this.targetMethodDefinition.returnType(String.class)
        .uri("/resources/{name}")
        .method(HttpMethod.GET)
        .templateParameter(0, new SimpleTemplateParameter("name"))
        .body(1);

    TargetMethodHandler targetMethodHandler = new BlockingTargetMethodHandler(
        this.targetMethodDefinition,
        this.encoder,
        null,
        this.client,
        this.decoder,
        this.exceptionHandler,
        this.executor,
        this.logger);

    targetMethodHandler.execute(Arrays.array("name", "body"));
    verify(encoder, times(1)).apply(any(), any(RequestSpecification.class));
    verify(client, times(1)).request(any(Request.class));
    verify(decoder, times(1)).decode(any(Response.class), eq(String.class));
    verifyZeroInteractions(this.exceptionHandler, interceptor);
  }

  @Test
  void skipEncoding_withNoBody() throws Throwable {
    when(this.client.request(any(Request.class))).thenReturn(this.response);
    this.targetMethodDefinition.returnType(String.class)
            .uri("/resources/{name}")
            .method(HttpMethod.GET)
            .templateParameter(0, new SimpleTemplateParameter("name"));

    TargetMethodHandler targetMethodHandler = new BlockingTargetMethodHandler(
        this.targetMethodDefinition,
        this.encoder,
        Collections.singletonList(this.interceptor),
        this.client,
        this.decoder,
        this.exceptionHandler,
        this.executor,
        this.logger);

    targetMethodHandler.execute(Arrays.array("name"));
    verify(client, times(1)).request(any(Request.class));
    verify(decoder, times(1)).decode(any(Response.class), eq(String.class));
    verify(interceptor, times(1)).accept(any(RequestSpecification.class));
    verifyZeroInteractions(this.exceptionHandler, this.encoder);
  }

  @Test
  void skipDecode_ifReturnType_isResponse() throws Throwable {
    when(this.client.request(any(Request.class))).thenReturn(this.response);
    this.targetMethodDefinition.returnType(Response.class)
        .uri("/resources/{name}")
        .method(HttpMethod.GET)
        .templateParameter(0, new SimpleTemplateParameter("name"));

    TargetMethodHandler targetMethodHandler = new BlockingTargetMethodHandler(
        this.targetMethodDefinition,
        this.encoder,
        Collections.singletonList(this.interceptor),
        this.client,
        this.decoder,
        this.exceptionHandler,
        this.executor,
        this.logger);

    Object result = targetMethodHandler.execute(Arrays.array("name"));
    verify(client, times(1)).request(any(Request.class));
    verify(interceptor, times(1)).accept(any(RequestSpecification.class));
    verifyZeroInteractions(this.exceptionHandler, this.encoder, this.decoder);
    assertThat(result).isInstanceOf(Response.class);
  }

  @Test
  void whenExceptionOccursBeforeRequest_exceptionHandlerIsCalled() {
    doThrow(new RuntimeException("Broken")).when(this.interceptor)
        .accept(any(RequestSpecification.class));
    this.targetMethodDefinition.returnType(Response.class)
        .uri("/resources/{name}")
        .method(HttpMethod.GET)
        .templateParameter(0, new SimpleTemplateParameter("name"))
        .readTimeout(1000)
        .body(1);

    TargetMethodHandler targetMethodHandler = new BlockingTargetMethodHandler(
        this.targetMethodDefinition,
        this.encoder,
        Collections.singletonList(this.interceptor),
        this.client,
        this.decoder,
        this.exceptionHandler,
        this.executor,
        this.logger);

    assertThrows(IllegalStateException.class,
        () -> targetMethodHandler.execute(Arrays.array("name")));
    verifyZeroInteractions(this.client, this.decoder);
    verify(this.exceptionHandler, times(1)).accept(any(Throwable.class));
  }

  @Test
  void whenExceptionOccursDuringRequest_exceptionHandlerIsCalled() {
    when(this.client.request(any(Request.class))).thenThrow(new RuntimeException("Broken"));
    this.targetMethodDefinition.returnType(Response.class)
        .uri("/resources/{name}")
        .method(HttpMethod.GET)
        .templateParameter(0, new SimpleTemplateParameter("name"));

    TargetMethodHandler targetMethodHandler = new BlockingTargetMethodHandler(
        this.targetMethodDefinition,
        this.encoder,
        Collections.singletonList(this.interceptor),
        this.client,
        this.decoder,
        this.exceptionHandler,
        this.executor,
        this.logger);

    assertThrows(IllegalStateException.class,
        () -> targetMethodHandler.execute(Arrays.array("name")));
    verify(this.client, times(1)).request(any(Request.class));
    verify(this.exceptionHandler, times(2)).accept(any(Throwable.class));
    verifyZeroInteractions(this.decoder);
  }

  interface TestInterface {

  }

}