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

package feign.decoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import feign.Response;
import feign.ResponseDecoder;
import feign.exception.FeignException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StringDecoderTest {

  private ResponseDecoder decoder = new StringDecoder();

  @Mock
  private Response response;

  @Test
  void feignException_whenTypeIsNotString() {
    when(response.body()).thenReturn(new ByteArrayInputStream("content".getBytes()));
    assertThrows(FeignException.class, () -> decoder.decode(response, List.class));
  }

  @Test
  void ioException_whenResponseCouldNotBeRead() throws Exception {
    when(response.body()).thenReturn(new ByteArrayInputStream("content".getBytes()));
    when(this.response.toByteArray()).thenThrow(new IOException("IO Exception"));
    assertThrows(FeignException.class, () -> decoder.decode(response, String.class));
  }

  @Test
  void decodeResponse_fromByteArray() throws IOException {
    when(response.body()).thenReturn(new ByteArrayInputStream("content".getBytes()));
    when(this.response.toByteArray()).thenReturn("content".getBytes(StandardCharsets.UTF_8));
    String result = this.decoder.decode(response, String.class);
    assertThat(result).isEqualTo("content");
  }

}