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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import feign.Response;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class StatusCodeConditionTest {

  private StatusCodeCondition condition = new StatusCodeCondition(503);

  @Test
  void shouldPass_ifCodeMatches() {
    Response response = mock(Response.class);
    when(response.status()).thenReturn(503);

    RetryContext context = new RetryContext(1, null, response);
    assertThat(condition.test(context)).isTrue();
  }

  @Test
  void shouldFail_ifCodeDoesNotMatch() {
    Response response = mock(Response.class);
    when(response.status()).thenReturn(200);

    RetryContext context = new RetryContext(1, null, response);
    assertThat(condition.test(context)).isFalse();
  }
}