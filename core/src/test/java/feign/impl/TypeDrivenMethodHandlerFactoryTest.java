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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import feign.Client;
import feign.ExceptionHandler;
import feign.FeignConfiguration;
import feign.Logger;
import feign.RequestEncoder;
import feign.ResponseDecoder;
import feign.Retry;
import feign.TargetMethodHandler;
import feign.contract.TargetMethodDefinition;
import feign.impl.type.TypeDefinitionFactory;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TypeDrivenMethodHandlerFactoryTest {

  @Mock
  private FeignConfiguration configuration;

  private final TypeDefinitionFactory typeDefinitionFactory = new TypeDefinitionFactory();

  private TargetMethodDefinition.Builder targetMethodDefinition;

  private TypeDrivenMethodHandlerFactory methodHandlerFactory;

  @BeforeEach
  void setUp() {
    this.methodHandlerFactory = new TypeDrivenMethodHandlerFactory();

    when(this.configuration.getRetry()).thenReturn(mock(Retry.class));
    when(this.configuration.getExceptionHandler()).thenReturn(mock(ExceptionHandler.class));
    when(this.configuration.getClient()).thenReturn(mock(Client.class));
    when(this.configuration.getLogger()).thenReturn(mock(Logger.class));
    when(this.configuration.getExecutor()).thenReturn(mock(Executor.class));
    when(this.configuration.getRequestEncoder()).thenReturn(mock(RequestEncoder.class));
    when(this.configuration.getResponseDecoder()).thenReturn(mock(ResponseDecoder.class));
    when(this.configuration.getRequestInterceptors())
        .thenReturn(Collections.emptyList());
  }

  @Test
  void createsBlockingHandler_byDefault() {
    this.targetMethodDefinition =
        TargetMethodDefinition.builder(Blog.class.getName());
    targetMethodDefinition.returnType(this.typeDefinitionFactory.create(String.class, Blog.class))
        .target(new AbsoluteUriTarget("http://localhost"));

    TargetMethodHandler targetMethodHandler =
        this.methodHandlerFactory
            .create(this.targetMethodDefinition.build(), this.configuration);
    assertThat(targetMethodHandler).isInstanceOf(BlockingTargetMethodHandler.class);
  }

  @Test
  void createsAsyncHandler_whenReturnType_isFuture() {
    this.targetMethodDefinition =
        TargetMethodDefinition.builder(Blog.class.getName());
    targetMethodDefinition.returnType(this.typeDefinitionFactory.create(Future.class, Blog.class))
        .target(new AbsoluteUriTarget("http://localhost"));

    TargetMethodHandler targetMethodHandler =
        this.methodHandlerFactory
            .create(this.targetMethodDefinition.build(), this.configuration);
    assertThat(targetMethodHandler).isInstanceOf(AsyncTargetMethodHandler.class);
  }

  @Test
  void createsAsyncHandler_whenReturnType_isCompletableFuture() {
    this.targetMethodDefinition =
        TargetMethodDefinition.builder(Blog.class.getName());
    targetMethodDefinition
        .returnType(this.typeDefinitionFactory.create(CompletableFuture.class, Blog.class))
        .target(new AbsoluteUriTarget("http://localhost"));

    TargetMethodHandler targetMethodHandler =
        this.methodHandlerFactory
            .create(this.targetMethodDefinition.build(), this.configuration);
    assertThat(targetMethodHandler).isInstanceOf(AsyncTargetMethodHandler.class);
  }

  interface Blog {

  }
}