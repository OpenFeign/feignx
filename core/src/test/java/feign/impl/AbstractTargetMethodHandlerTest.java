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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import feign.Client;
import feign.ExceptionHandler;
import feign.ExceptionHandler.RethrowExceptionHandler;
import feign.FeignConfiguration;
import feign.Logger;
import feign.Request;
import feign.RequestEncoder;
import feign.RequestEntity;
import feign.RequestInterceptor;
import feign.Response;
import feign.ResponseDecoder;
import feign.Retry;
import feign.contract.TargetMethodDefinition;
import feign.TargetMethodHandler;
import feign.contract.TargetMethodParameterDefinition;
import feign.http.HttpMethod;
import feign.http.RequestSpecification;
import feign.impl.type.TypeDefinition;
import feign.impl.type.TypeDefinitionFactory;
import feign.retry.NoRetry;
import feign.template.ExpanderRegistry;
import feign.template.expander.CachingExpanderRegistry;
import feign.template.expander.DefaultExpander;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("ThrowableNotThrown")
@ExtendWith(MockitoExtension.class)
class AbstractTargetMethodHandlerTest {

  private TargetMethodDefinition.Builder targetMethodDefinition;

  @Mock
  private RequestEncoder encoder;

  @Mock
  private Client client;

  @Mock
  private ResponseDecoder decoder;

  @Spy
  private final ExceptionHandler exceptionHandler = new RethrowExceptionHandler();

  @Mock
  private Response response;

  @Mock
  private Logger logger;

  @Mock
  private FeignConfiguration configuration;

  private final TypeDefinitionFactory typeDefinitionFactory = new TypeDefinitionFactory();

  private final RequestInterceptor interceptor = RequestInterceptor.identity();

  private final Retry retry = new NoRetry();

  private final Executor executor = Executors.newSingleThreadExecutor();

  @BeforeEach
  void setUp() {
    this.targetMethodDefinition = TargetMethodDefinition.builder(TestInterface.class.getName());
    when(this.configuration.getRetry()).thenReturn(this.retry);
    when(this.configuration.getExceptionHandler()).thenReturn(this.exceptionHandler);
    when(this.configuration.getClient()).thenReturn(this.client);
    when(this.configuration.getLogger()).thenReturn(this.logger);
    when(this.configuration.getExecutor()).thenReturn(this.executor);
    when(this.configuration.getRequestEncoder()).thenReturn(this.encoder);
    when(this.configuration.getResponseDecoder()).thenReturn(this.decoder);
    when(this.configuration.getRequestInterceptors())
        .thenReturn(Collections.singletonList(this.interceptor));
  }

  @Test
  void interceptors_areApplied_ifPresent() throws Throwable {
    when(this.client.request(any(Request.class))).thenReturn(this.response);
    when(this.response.body()).thenReturn(mock(InputStream.class));
    TypeDefinition returnType = this.typeDefinitionFactory
        .create(String.class, TestInterface.class);
    this.targetMethodDefinition.returnType(returnType)
        .target(new AbsoluteUriTarget("http://localhost"))
        .uri("/resources/{name}")
        .method(HttpMethod.GET)
        .parameterDefinition(0, TargetMethodParameterDefinition.builder()
            .name("name")
            .type(String.class.getName())
            .index(0)
            .expanderClassName(DefaultExpander.class.getName())
            .build())
        .body(1);
    TargetMethodDefinition methodDefinition = this.targetMethodDefinition.build();
    TargetMethodHandler targetMethodHandler = new BlockingTargetMethodHandler(
        methodDefinition,
        this.configuration);

    targetMethodHandler.execute(Arrays.array("name", "body"));
    verify(encoder, times(1)).apply(any(), any(RequestSpecification.class));
    verify(client, times(1)).request(any(Request.class));
    verify(decoder, times(1)).decode(any(Response.class), eq(String.class));
    verifyZeroInteractions(this.exceptionHandler);
  }

  @Test
  void interceptors_areNotApplied_ifNotPresent() throws Throwable {
    when(this.client.request(any(Request.class))).thenReturn(this.response);
    when(this.response.body()).thenReturn(mock(InputStream.class));
    TypeDefinition returnType = this.typeDefinitionFactory
        .create(String.class, TestInterface.class);
    this.targetMethodDefinition.returnType(returnType)
        .target(new AbsoluteUriTarget("http://localhost"))
        .uri("/resources/{name}")
        .method(HttpMethod.GET)
        .parameterDefinition(0, TargetMethodParameterDefinition.builder()
            .name("name")
            .type(String.class.getName())
            .index(0)
            .expanderClassName(DefaultExpander.class.getName())
            .build())
        .body(1);

    TargetMethodDefinition methodDefinition = this.targetMethodDefinition.build();
    when(this.configuration.getRequestInterceptors()).thenReturn(Collections.emptyList());
    TargetMethodHandler targetMethodHandler = new BlockingTargetMethodHandler(
        methodDefinition,
        this.configuration);

    targetMethodHandler.execute(Arrays.array("name", "body"));
    verify(encoder, times(1)).apply(any(), any(RequestSpecification.class));
    verify(client, times(1)).request(any(Request.class));
    verify(decoder, times(1)).decode(any(Response.class), eq(String.class));
    verifyZeroInteractions(this.exceptionHandler);
  }

  @Test
  void encode_whenBodyPresent() throws Throwable {
    when(this.client.request(any(Request.class))).thenReturn(this.response);
    when(this.response.body()).thenReturn(mock(InputStream.class));
    when(this.encoder.apply(any(), any(RequestSpecification.class))).thenReturn(
        new RequestEntity() {
          @Override
          public Optional<Charset> getCharset() {
            return Optional.empty();
          }

          @Override
          public int getContentLength() {
            return 0;
          }

          @Override
          public String getContentType() {
            return null;
          }

          @Override
          public byte[] getData() {
            return new byte[0];
          }
        });

    TypeDefinition returnType = this.typeDefinitionFactory
        .create(String.class, TestInterface.class);
    this.targetMethodDefinition.returnType(returnType)
        .target(new AbsoluteUriTarget("http://localhost"))
        .uri("/resources/{name}")
        .method(HttpMethod.GET)
        .parameterDefinition(0, TargetMethodParameterDefinition.builder()
            .name("name")
            .type(String.class.getName())
            .index(0)
            .expanderClassName(DefaultExpander.class.getName())
            .build())
        .body(1);

    TargetMethodDefinition methodDefinition = this.targetMethodDefinition.build();
    TargetMethodHandler targetMethodHandler = new BlockingTargetMethodHandler(
        methodDefinition,
        this.configuration);

    targetMethodHandler.execute(Arrays.array("name", "body"));
    verify(client, times(1)).request(any(Request.class));
    verify(decoder, times(1)).decode(any(Response.class), eq(String.class));
    verify(encoder, times(1)).apply(any(), any(RequestSpecification.class));
    verifyZeroInteractions(this.exceptionHandler);
  }

  @Test
  void skipEncoding_withNoBody() throws Throwable {
    when(this.client.request(any(Request.class))).thenReturn(this.response);
    when(this.response.body()).thenReturn(mock(InputStream.class));

    TypeDefinition returnType = this.typeDefinitionFactory
        .create(String.class, TestInterface.class);

    this.targetMethodDefinition.returnType(returnType)
        .target(new AbsoluteUriTarget("http://localhost"))
        .uri("/resources/{name}")
        .method(HttpMethod.GET)
        .parameterDefinition(0, TargetMethodParameterDefinition.builder()
            .name("name")
            .type(String.class.getName())
            .index(0)
            .expanderClassName(DefaultExpander.class.getName())
            .build());

    TargetMethodDefinition methodDefinition = this.targetMethodDefinition.build();
    TargetMethodHandler targetMethodHandler = new BlockingTargetMethodHandler(
        methodDefinition,
        this.configuration);

    targetMethodHandler.execute(Arrays.array("name"));
    verify(client, times(1)).request(any(Request.class));
    verify(decoder, times(1)).decode(any(Response.class), eq(String.class));
    verifyZeroInteractions(this.exceptionHandler, this.encoder);
  }

  @Test
  void skipDecode_ifReturnType_isResponse() throws Throwable {
    when(this.client.request(any(Request.class))).thenReturn(this.response);
    when(this.response.body()).thenReturn(mock(InputStream.class));

    TypeDefinition returnType = this.typeDefinitionFactory
        .create(Response.class, TestInterface.class);

    this.targetMethodDefinition.returnType(returnType)
        .target(new AbsoluteUriTarget("http://localhost"))
        .uri("/resources/{name}")
        .method(HttpMethod.GET)
        .parameterDefinition(0, TargetMethodParameterDefinition.builder()
            .name("name")
            .type(String.class.getName())
            .index(0)
            .expanderClassName(DefaultExpander.class.getName())
            .build());

    TargetMethodDefinition methodDefinition = this.targetMethodDefinition.build();
    TargetMethodHandler targetMethodHandler = new BlockingTargetMethodHandler(
        methodDefinition,
        this.configuration);

    Object result = targetMethodHandler.execute(Arrays.array("name"));
    verify(client, times(1)).request(any(Request.class));
    verifyZeroInteractions(this.exceptionHandler, this.encoder, this.decoder);
    assertThat(result).isInstanceOf(Response.class);
  }

  @Test
  void skipDecode_ifResponseIsNull() throws Throwable {

    TypeDefinition returnType = this.typeDefinitionFactory
        .create(Response.class, TestInterface.class);

    this.targetMethodDefinition.returnType(returnType)
        .target(new AbsoluteUriTarget("http://localhost"))
        .uri("/resources/{name}")
        .method(HttpMethod.GET)
        .parameterDefinition(0, TargetMethodParameterDefinition.builder()
            .name("name")
            .type(String.class.getName())
            .index(0)
            .expanderClassName(DefaultExpander.class.getName())
            .build());

    TargetMethodDefinition methodDefinition = this.targetMethodDefinition.build();
    TargetMethodHandler targetMethodHandler = new BlockingTargetMethodHandler(
        methodDefinition,
        this.configuration);

    Object result = targetMethodHandler.execute(Arrays.array("name"));
    verify(client, times(1)).request(any(Request.class));
    verifyZeroInteractions(this.exceptionHandler, this.encoder, this.decoder);
    assertThat(result).isNull();
  }

  @Test
  void skipDecode_ifResponseBody_isNull() throws Throwable {
    when(this.client.request(any(Request.class))).thenReturn(this.response);

    TypeDefinition returnType = this.typeDefinitionFactory
        .create(Response.class, TestInterface.class);

    this.targetMethodDefinition.returnType(returnType)
        .target(new AbsoluteUriTarget("http://localhost"))
        .uri("/resources/{name}")
        .method(HttpMethod.GET)
        .parameterDefinition(0, TargetMethodParameterDefinition.builder()
            .name("name")
            .type(String.class.getName())
            .index(0)
            .expanderClassName(DefaultExpander.class.getName())
            .build());

    TargetMethodDefinition methodDefinition = this.targetMethodDefinition.build();
    TargetMethodHandler targetMethodHandler = new BlockingTargetMethodHandler(
        methodDefinition,
        this.configuration);

    Object result = targetMethodHandler.execute(Arrays.array("name"));
    verify(client, times(1)).request(any(Request.class));
    verifyZeroInteractions(this.exceptionHandler, this.encoder, this.decoder);
    assertThat(result).isNull();
  }

  @Test
  void skipDecode_ifReturnType_Void() throws Throwable {
    when(this.client.request(any(Request.class))).thenReturn(this.response);

    TypeDefinition returnType = this.typeDefinitionFactory
        .create(void.class, TestInterface.class);
    this.targetMethodDefinition.returnType(returnType)
        .target(new AbsoluteUriTarget("http://localhost"))
        .uri("/resources/{name}")
        .method(HttpMethod.GET)
        .parameterDefinition(0, TargetMethodParameterDefinition.builder()
            .name("name")
            .type(String.class.getName())
            .index(0)
            .expanderClassName(DefaultExpander.class.getName())
            .build());

    TargetMethodDefinition methodDefinition = this.targetMethodDefinition.build();
    TargetMethodHandler targetMethodHandler = new BlockingTargetMethodHandler(
        methodDefinition,
        this.configuration);

    Object result = targetMethodHandler.execute(Arrays.array("name"));
    verify(client, times(1)).request(any(Request.class));
    verifyZeroInteractions(this.exceptionHandler, this.encoder, this.decoder);
    assertThat(result).isNull();
  }

  @Test
  void whenExceptionOccursBeforeRequest_exceptionHandlerIsCalled() {
    RequestInterceptor runtimeBroken = specification -> {
      throw new RuntimeException("Broken");
    };

    TypeDefinition returnType = this.typeDefinitionFactory
        .create(Response.class, TestInterface.class);

    this.targetMethodDefinition.returnType(returnType)
        .target(new AbsoluteUriTarget("http://localhost"))
        .uri("/resources/{name}")
        .method(HttpMethod.GET)
        .parameterDefinition(0, TargetMethodParameterDefinition.builder()
            .name("name")
            .type(String.class.getName())
            .index(0)
            .expanderClassName(DefaultExpander.class.getName())
            .build())
        .readTimeout(1000)
        .body(1);

    TargetMethodDefinition methodDefinition = this.targetMethodDefinition.build();
    when(this.configuration.getRequestInterceptors())
        .thenReturn(Collections.singletonList(runtimeBroken));
    TargetMethodHandler targetMethodHandler = new BlockingTargetMethodHandler(
        methodDefinition,
        this.configuration);

    assertThrows(RuntimeException.class,
        () -> targetMethodHandler.execute(Arrays.array("name")));
    verifyZeroInteractions(this.client, this.decoder);
    verify(this.exceptionHandler, times(1)).apply(any(Throwable.class));
  }

  @Test
  void whenExceptionOccursDuringRequest_exceptionHandlerIsCalled() {
    when(this.client.request(any(Request.class))).thenThrow(new RuntimeException("Broken"));

    TypeDefinition returnType = this.typeDefinitionFactory
        .create(String.class, TestInterface.class);

    this.targetMethodDefinition.returnType(returnType)
        .target(new AbsoluteUriTarget("http://localhost"))
        .uri("/resources/{name}")
        .method(HttpMethod.GET)
        .parameterDefinition(0, TargetMethodParameterDefinition.builder()
            .name("name")
            .type(String.class.getCanonicalName())
            .index(0)
            .expanderClassName(DefaultExpander.class.getName())
            .build());

    TargetMethodDefinition methodDefinition = this.targetMethodDefinition.build();
    TargetMethodHandler targetMethodHandler = new BlockingTargetMethodHandler(
        methodDefinition,
        this.configuration);

    assertThrows(RuntimeException.class,
        () -> targetMethodHandler.execute(Arrays.array("name")));
    verify(this.client, times(1)).request(any(Request.class));
    verify(this.exceptionHandler, times(1)).apply(any(Throwable.class));
    verifyZeroInteractions(this.decoder);
  }

  @Test
  void exceptionHandlerCalled_ifErrorDuringDecode() {
    when(this.client.request(any(Request.class))).thenReturn(this.response);
    when(this.response.body()).thenReturn(mock(InputStream.class));
    when(this.decoder.decode(any(Response.class), any())).thenThrow(new RuntimeException("bad"));

    TypeDefinition returnType = this.typeDefinitionFactory
        .create(String.class, TestInterface.class);

    this.targetMethodDefinition.returnType(returnType)
        .target(new AbsoluteUriTarget("http://localhost"))
        .uri("/resources/{name}")
        .method(HttpMethod.GET)
        .parameterDefinition(0, TargetMethodParameterDefinition.builder()
            .name("name")
            .type(String.class.getCanonicalName())
            .index(0)
            .expanderClassName(DefaultExpander.class.getName())
            .build())
        .body(1);

    ExceptionHandler exceptionHandler = spy(new RethrowExceptionHandler());
    TargetMethodDefinition methodDefinition = this.targetMethodDefinition.build();
    when(this.configuration.getExceptionHandler()).thenReturn(exceptionHandler);
    TargetMethodHandler targetMethodHandler = new BlockingTargetMethodHandler(
        methodDefinition,
        this.configuration);

    assertThrows(RuntimeException.class,
        () -> targetMethodHandler.execute(Arrays.array("name", "body")));
    verify(encoder, times(1)).apply(any(), any(RequestSpecification.class));
    verify(client, times(1)).request(any(Request.class));
    verify(decoder, times(1)).decode(any(Response.class), eq(String.class));
    verify(exceptionHandler, times(1)).apply(any(Throwable.class));
  }

  @SuppressWarnings("unchecked")
  @Test
  void ensureTemplateParameters_areCached() {

    TypeDefinition returnType = this.typeDefinitionFactory
        .create(String.class, TestInterface.class);

    this.targetMethodDefinition.returnType(returnType)
        .target(new AbsoluteUriTarget("http://localhost"))
        .uri("/resources/{name}")
        .method(HttpMethod.GET)
        .parameterDefinition(0, TargetMethodParameterDefinition.builder()
            .name("name")
            .type(String.class.getCanonicalName())
            .index(0)
            .expanderClassName(DefaultExpander.class.getName())
            .build())
        .body(1);

    ExpanderRegistry expanderRegistry = spy(new CachingExpanderRegistry());
    BlockingTargetMethodHandler targetMethodHandler = new BlockingTargetMethodHandler(
        this.targetMethodDefinition.build(),
        this.configuration);
    targetMethodHandler.setExpanderRegistry(expanderRegistry);

    /* call the method twice, expect the expander registry to be invoked only once */
    targetMethodHandler.execute(Arrays.array("name", "body"));
    targetMethodHandler.execute(Arrays.array("name", "body"));

    verify(expanderRegistry, times(1))
        .getExpander(any(Class.class), anyString());

  }

  interface TestInterface {

  }

}