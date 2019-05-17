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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import feign.Client;
import feign.Contract;
import feign.ExceptionHandler;
import feign.ExceptionHandler.RethrowExceptionHandler;
import feign.Logger;
import feign.RequestEncoder;
import feign.Response;
import feign.ResponseDecoder;
import feign.TargetMethodDefinition;
import feign.contract.FeignContract;
import feign.contract.Request;
import feign.impl.AsyncTargetMethodHandlerTest.Blog.Post;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AsyncTargetMethodHandlerTest {

  @Mock
  private RequestEncoder encoder;

  @Mock
  private Client client;

  @Mock
  private ResponseDecoder decoder;

  @Spy
  private ExceptionHandler exceptionHandler = new RethrowExceptionHandler();

  @Mock
  private Logger logger;

  @Mock
  private Response response;

  @Captor
  private ArgumentCaptor<Class<?>> classArgumentCaptor;

  private Contract contract = new FeignContract();

  private AsyncTargetMethodHandler methodHandler;

  @BeforeEach
  void setUp() {
    Collection<TargetMethodDefinition> methodDefinitions =
        this.contract.apply(new UriTarget<>(Blog.class, "https://www.example.com"));
    TargetMethodDefinition targetMethodDefinition = methodDefinitions.stream()
        .findFirst().get();
    this.methodHandler = new AsyncTargetMethodHandler(
        targetMethodDefinition,
        encoder,
        Collections.emptyList(),
        client,
        decoder,
        exceptionHandler,
        Executors.newFixedThreadPool(10),
        logger);
  }

  @SuppressWarnings("unchecked")
  @Test
  void returnWrappedFuture_onSuccess() throws Exception {
    when(this.client.request(any(feign.Request.class))).thenReturn(this.response);
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
  void throwException_onFailure() throws Exception {
    when(this.client.request(any(feign.Request.class))).thenThrow(new RuntimeException("Failed"));
    Object result = this.methodHandler.execute(new Object[]{});

    /* ensure that the method handler returned a future, which contains a string */
    assertThat(result).isInstanceOf(CompletableFuture.class);
    CompletableFuture<String> future = (CompletableFuture<String>) result;
    assertThrows(ExecutionException.class, future::get);

    assertThat(future).isCompletedExceptionally();
    verifyZeroInteractions(this.decoder);
    verify(this.exceptionHandler, times(1)).accept(any(Throwable.class));
  }

  @SuppressWarnings("unchecked")
  @Test
  void methodNotHandled_returnsNull() throws Exception {
    Collection<TargetMethodDefinition> methodDefinitions =
        this.contract.apply(new UriTarget<>(Blog.class, "https://www.example.com"));
    TargetMethodDefinition targetMethodDefinition = methodDefinitions.stream()
        .findFirst().get();
    ExceptionHandler mockHandler = mock(ExceptionHandler.class);
    this.methodHandler = new AsyncTargetMethodHandler(
        targetMethodDefinition,
        encoder,
        Collections.emptyList(),
        client,
        decoder,
        mockHandler,
        Executors.newFixedThreadPool(10),
        logger);

    when(this.client.request(any(feign.Request.class))).thenThrow(new RuntimeException("Failed"));
    Object result = this.methodHandler.execute(new Object[]{});

    /* ensure that the method handler returned a future, which contains a string */
    assertThat(result).isInstanceOf(CompletableFuture.class);
    CompletableFuture<String> future = (CompletableFuture<String>) result;
    future.get();

    assertThat(future).isCompleted();
    assertThat(future).isCompletedWithValue(null);
    verifyZeroInteractions(this.decoder);
    verify(mockHandler, times(1)).accept(any(Throwable.class));
  }

  interface Blog {
    @Request(value = "/")
    CompletableFuture<Post> getPosts();

    class Post {

    }
  }

}