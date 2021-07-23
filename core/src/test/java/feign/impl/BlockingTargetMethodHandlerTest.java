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
import feign.TargetMethodHandler;
import feign.contract.Request;
import feign.contract.TargetMethodDefinition;
import feign.http.HttpMethod;
import feign.retry.NoRetry;
import feign.support.AuditingExecutor;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BlockingTargetMethodHandlerTest {

  @Mock
  private FeignConfiguration configuration;

  @Test
  void usingDefaultExecutor_willUseTheCallingThread() throws Throwable {
    AuditingExecutor executor = new AuditingExecutor();

    when(this.configuration.getRetry()).thenReturn(new NoRetry());
    when(this.configuration.getExceptionHandler()).thenReturn(mock(ExceptionHandler.class));
    when(this.configuration.getClient()).thenReturn(mock(Client.class));
    when(this.configuration.getLogger()).thenReturn(mock(Logger.class));
    when(this.configuration.getExecutor()).thenReturn(executor);
    when(this.configuration.getRequestEncoder()).thenReturn(mock(RequestEncoder.class));
    when(this.configuration.getResponseDecoder()).thenReturn(mock(ResponseDecoder.class));
    when(this.configuration.getRequestInterceptors())
        .thenReturn(Collections.emptyList());

    TargetMethodDefinition.Builder builder = TargetMethodDefinition.builder(Blog.class.getName());
    builder.returnTypeFullyQualifiedClassName(void.class.getName())
        .uri("/resources/{name}")
        .method(HttpMethod.GET);

    TargetMethodDefinition methodDefinition = builder.build();
    TargetMethodHandler blockingHandler =
        new BlockingTargetMethodHandler(
            methodDefinition, this.configuration);

    /* get the current thread id */
    long currentThread = Thread.currentThread().getId();

    /* execute the request */
    blockingHandler.execute(new Object[]{});

    /* make sure that the executor used the current thread only */
    assertThat(executor.getThreads()).containsOnly(currentThread);
    assertThat(executor.getExecutionCount()).isEqualTo(6);
  }

  @SuppressWarnings("unused")
  interface Blog {

    @Request(value = "/")
    Post getPosts();

    class Post {

    }
  }
}