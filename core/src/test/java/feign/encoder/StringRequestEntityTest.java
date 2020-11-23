/*
 * Copyright 2019-2020 OpenFeign Contributors
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

package feign.encoder;


import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/**
 * Unit Test for StringRequestEntity.
 */
class StringRequestEntityTest {

  @Test
  void canCreate() {
    final String content = "content";
    final int contentLength = content.getBytes(StandardCharsets.UTF_8).length;

    StringRequestEntity requestEntity = new StringRequestEntity(content);
    assertThat(requestEntity).isNotNull();
    assertThat(requestEntity.getCharset()).isPresent()
        .isEqualTo(StandardCharsets.UTF_8);
    assertThat(requestEntity.getContentType()).isNotEmpty()
        .isEqualToIgnoringCase(StringRequestEntity.TEXT_PLAIN);
    assertThat(requestEntity.getData()).isNotEmpty()
        .hasSize(contentLength);
    assertThat(requestEntity.getContentLength()).isEqualTo(contentLength);
  }
}