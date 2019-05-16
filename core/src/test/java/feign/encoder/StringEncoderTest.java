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

package feign.encoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import feign.RequestEncoder;
import feign.http.RequestSpecification;
import org.junit.jupiter.api.Test;

class StringEncoderTest {

  @Test
  void apply_toString() {
    RequestEncoder encoder = new StringEncoder();
    RequestSpecification requestSpecification = mock(RequestSpecification.class);
    encoder.apply("content", requestSpecification);
    verify(requestSpecification, times(1)).content(any(byte[].class));
  }

  @Test
  void apply_withNullSkips() {
    RequestEncoder encoder = new StringEncoder();
    RequestSpecification requestSpecification = mock(RequestSpecification.class);
    encoder.apply(null, requestSpecification);
    verifyZeroInteractions(requestSpecification);
  }
}