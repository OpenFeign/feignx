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

import feign.Client;
import feign.ExceptionHandler;
import feign.Logger;
import feign.RequestEncoder;
import feign.ResponseDecoder;
import feign.TargetMethodDefinition;
import feign.TargetMethodHandler;
import feign.contract.Request;
import feign.http.HttpMethod;
import feign.support.AuditingExecutor;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BlockingTargetMethodHandlerTest {

  @Mock
  private RequestEncoder encoder;

  @Mock
  private Client client;

  @Mock
  private ResponseDecoder decoder;

  @Mock
  private ExceptionHandler exceptionHandler;

  @Mock
  private Logger logger;

  @Test
  void usingDefaultExecutor_willUseTheCallingThread() throws Throwable {
    AuditingExecutor executor = new AuditingExecutor();
    TargetMethodDefinition methodDefinition = new TargetMethodDefinition(
        new UriTarget<>(Blog.class, "https://www.example.com"));
    TargetMethodHandler blockingHandler = new BlockingTargetMethodHandler(methodDefinition, encoder,
        Collections.emptyList(), client, decoder, exceptionHandler, executor, logger);

    methodDefinition.returnType(void.class)
        .uri("/resources/{name}")
        .method(HttpMethod.GET);

    /* get the current thread id */
    long currentThread = Thread.currentThread().getId();

    /* execute the request */
    blockingHandler.execute(new Object[]{});

    /* make sure that the executor used the current thread only */
    assertThat(executor.getThreads()).containsOnly(currentThread);
    assertThat(executor.getExecutionCount()).isEqualTo(5);
  }

  interface Blog {

    @Request(value = "/")
    Post getPosts();

    class Post {

    }
  }
}