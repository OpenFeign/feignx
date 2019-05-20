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

package feign.retry;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import feign.Client;
import feign.Request;
import feign.Retry;
import org.junit.jupiter.api.Test;

class NoRetryTest {

  @Test
  void callbackShouldBeCalled() throws Throwable {
    Retry retry = new NoRetry();
    Client client = mock(Client.class);
    retry.execute("test", mock(Request.class), (client::request));
    verify(client, times(1)).request(any(Request.class));
  }
}