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

package feign.template.expander;

import static org.assertj.core.api.Assertions.assertThat;

import feign.contract.Param;
import org.junit.jupiter.api.Test;

class ExpanderUtilsTest {

  @Test
  void enum_isSimple() {
    Constants constants = Constants.ONE;
    assertThat(ExpanderUtils.isSimpleType(constants.getClass())).isTrue();
  }

  @Test
  void array_isSimple() {
    int[] numbers = new int[10];
    assertThat(ExpanderUtils.isSimpleType(numbers.getClass())).isTrue();
  }

  @Test
  void annotation_isSimple() {
    assertThat(ExpanderUtils.isSimpleType(Param.class)).isTrue();
  }

  @Test
  void primitive_isSimple() {
    assertThat(ExpanderUtils.isSimpleType(int.class)).isTrue();
  }

  @Test
  void number_isNotSimple() {
    assertThat(ExpanderUtils.isSimpleType(Integer.class)).isFalse();
  }

  @Test
  void string_isSimple() {
    assertThat(ExpanderUtils.isSimpleType(String.class)).isTrue();
  }

  @Test
  void iterable_isSimple() {
    assertThat(ExpanderUtils.isSimpleType(Iterable.class)).isTrue();
  }

  @Test
  void map_isSimple() {
    assertThat(ExpanderUtils.isSimpleType(Iterable.class)).isTrue();
  }

  @Test
  void pojo_isNotSimple() {
    assertThat(ExpanderUtils.isSimpleType(SimpleObject.class)).isFalse();

  }

  enum Constants {
    ONE
  }

  private class SimpleObject {

  }
}