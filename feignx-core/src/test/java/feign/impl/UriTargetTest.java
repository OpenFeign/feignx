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
import static org.junit.jupiter.api.Assertions.*;

import feign.Target;
import org.junit.jupiter.api.Test;

class UriTargetTest {

  @Test
  void canCreate_withTypeAsName() {
    Target<?> target = new UriTarget<>(String.class, "https://www.example.com");
    assertThat(target.name()).isEqualTo("String");
  }

  @Test
  void canCreate_withCustomName() {
    Target<?> target = new UriTarget<>(String.class, "Sample", "https://www.example.com");
    assertThat(target.name()).isEqualTo("Sample");
  }

  @Test
  void uri_mustBeAbsolute() {
    assertThrows(IllegalStateException.class,
        () -> new UriTarget<>(String.class, "/relative"));
  }

  @Test
  void isEqual_whenNameAndTypeMatch() {
    Target<?> one = new UriTarget<>(String.class, "https://www.example.com");
    Target<?> two = new UriTarget<>(String.class, "https://www.example.com");
    assertThat(one).isEqualTo(two);
  }

  @Test
  void isNotEqual_whenNameAndTypeDoNotMatch() {
    Target<?> one = new UriTarget<>(String.class, "https://www.example.com");
    Target<?> two = new UriTarget<>(String.class, "another", "https://www.example.com");
    assertThat(one).isNotEqualTo(two);
  }
}