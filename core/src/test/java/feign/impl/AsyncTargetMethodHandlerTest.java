/*
 * Copyright 2019-2022 OpenFeign Contributors
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
import static org.mockito.Mockito.*;

import feign.Client;
import feign.Contract;
import feign.ExceptionHandler;
import feign.ExceptionHandler.RethrowExceptionHandler;
import feign.FeignConfiguration;
import feign.Logger;
import feign.RequestEncoder;
import feign.Response;
import feign.ResponseDecoder;
import feign.TargetMethodHandler;
import feign.contract.FeignContract;
import feign.contract.Request;
import feign.contract.TargetDefinition;
import feign.contract.TargetMethodDefinition;
import feign.impl.AsyncTargetMethodHandlerTest.Blog.Post;
import feign.retry.NoRetry;
import feign.support.AuditingExecutor;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("ThrowableNotThrown")
@ExtendWith(MockitoExtension.class)
class AsyncTargetMethodHandlerTest {

  @Mock
  private FeignConfiguration configuration;

  @Mock
  private Client client;

  @Mock
  private ResponseDecoder decoder;

  @Spy
  private final ExceptionHandler exceptionHandler = new RethrowExceptionHandler();

  @Mock
  private Response response;

  @Captor
  private ArgumentCaptor<Class<?>> classArgumentCaptor;

  private final Contract contract = new FeignContract();

  private AsyncTargetMethodHandler methodHandler;

  private final Executor executor = Executors.newFixedThreadPool(10);

  @BeforeEach
  void setUp() {
    when(this.configuration.getRetry()).thenReturn(new NoRetry());
    when(this.configuration.getExceptionHandler()).thenReturn(this.exceptionHandler);
    when(this.configuration.getClient()).thenReturn(this.client);
    when(this.configuration.getLogger()).thenReturn(mock(Logger.class));
    when(this.configuration.getExecutor()).thenReturn(this.executor);
    when(this.configuration.getRequestEncoder()).thenReturn(mock(RequestEncoder.class));
    when(this.configuration.getResponseDecoder()).thenReturn(this.decoder);
    when(this.configuration.getRequestInterceptors())
        .thenReturn(Collections.emptyList());
    when(this.configuration.getTarget()).thenReturn(new AbsoluteUriTarget("http://localhost"));

    TargetDefinition definition = this.contract.apply(Blog.class, this.configuration);

    //noinspection OptionalGetWithoutIsPresent
    TargetMethodDefinition targetMethodDefinition = definition.getMethodDefinitions().stream()
        .findFirst().get();

    this.methodHandler = new AsyncTargetMethodHandler(
        targetMethodDefinition,
        this.configuration);
  }

  @SuppressWarnings("unchecked")
  @Test
  void returnWrappedFuture_onSuccess() throws Exception {
    when(this.client.request(any(feign.Request.class))).thenReturn(this.response);
    when(this.response.body()).thenReturn(mock(InputStream.class));
    when(this.decoder.decode(any(Response.class), any())).thenReturn("results");
    Object result = this.methodHandler.execute(new Object[]{});

    /* ensure that the method handler returned a future, which contains a string */
    assertThat(result).isInstanceOf(CompletableFuture.class);
    CompletableFuture<String> future = (CompletableFuture<String>) result;
    future.get();

    /* capture the call to the decoder, this should be the contained type and not a future */
    verify(this.decoder).decode(any(Response.class), classArgumentCaptor.capture());
    assertThat(classArgumentCaptor.getValue()).isAssignableFrom(Post.class);
    assertThat(future).isCompletedWithValue("results");
  }

  @SuppressWarnings("unchecked")
  @Test
  void throwException_onFailure() {
    when(this.client.request(any(feign.Request.class))).thenThrow(new RuntimeException("Failed"));
    Object result = this.methodHandler.execute(new Object[]{});

    /* ensure that the method handler returned a future, which contains a string */
    assertThat(result).isInstanceOf(CompletableFuture.class);
    CompletableFuture<String> future = (CompletableFuture<String>) result;
    assertThrows(ExecutionException.class, future::get);

    assertThat(future).isCompletedExceptionally();
    verifyNoInteractions(this.decoder);
    verify(this.exceptionHandler, times(1)).apply(any(Throwable.class));
  }

  @SuppressWarnings({"unchecked", "OptionalGetWithoutIsPresent"})
  @Test
  void methodNotHandled_returnsNull() {
    TargetDefinition definition = this.contract.apply(Blog.class, this.configuration);
    TargetMethodDefinition targetMethodDefinition = definition.getMethodDefinitions().stream()
        .findFirst().get();
    ExceptionHandler mockHandler = mock(ExceptionHandler.class);
    when(this.configuration.getExceptionHandler()).thenReturn(mockHandler);
    this.methodHandler = new AsyncTargetMethodHandler(
        targetMethodDefinition,
        this.configuration);

    when(this.client.request(any(feign.Request.class))).thenThrow(new RuntimeException("Failed"));
    Object result = this.methodHandler.execute(new Object[]{});

    /* ensure that the method handler returned a future, which contains a string */
    assertThat(result).isInstanceOf(CompletableFuture.class);
    CompletableFuture<String> future = (CompletableFuture<String>) result;
    assertThrows(ExecutionException.class, future::get);

    assertThat(future).isCompletedExceptionally();
    verifyNoInteractions(this.decoder);
    verify(mockHandler, times(1)).apply(any(Throwable.class));
  }

  @SuppressWarnings({"unchecked", "OptionalGetWithoutIsPresent"})
  @Test
  void usingMultiThreadedExecutor_willExecuteOnOtherThreads() throws Throwable {
    AuditingExecutor executor = new AuditingExecutor(this.executor);

    TargetDefinition definition = this.contract.apply(Blog.class, this.configuration);
    Collection<TargetMethodDefinition> methodDefinitions = definition.getMethodDefinitions();

    TargetMethodDefinition targetMethodDefinition = methodDefinitions.stream()
        .findFirst().get();
    when(this.configuration.getExecutor()).thenReturn(executor);
    TargetMethodHandler asyncTargetMethodHandler =
        new AsyncTargetMethodHandler(
            targetMethodDefinition,
            this.configuration);

    /* get the current thread id */
    long currentThread = Thread.currentThread().getId();

    /* execute the request */
    CompletableFuture<Object> result =
        (CompletableFuture<Object>) asyncTargetMethodHandler.execute(new Object[]{});
    result.get();

    /* make sure that the executor used different threads */
    assertThat(executor.getThreads()).doesNotHaveDuplicates()
        .hasSizeGreaterThan(1).contains(currentThread);
    assertThat(executor.getExecutionCount()).isEqualTo(6);
  }

  @SuppressWarnings("unused")
  interface Blog {

    @Request(value = "/")
    CompletableFuture<Post> getPosts();

    class Post {

    }
  }

}