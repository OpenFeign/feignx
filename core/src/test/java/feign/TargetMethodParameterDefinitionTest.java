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

package feign;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import org.junit.jupiter.api.Test;

class TargetMethodParameterDefinitionTest {

  @Test
  void create_withExpander() {
    TargetMethodParameterDefinition parameterDefinition = TargetMethodParameterDefinition.builder()
        .name("parameter")
        .index(0)
        .expanderClassName("io.openfeign.expander.StringExpander")
        .build();
    assertThat(parameterDefinition.getName()).isNotEmpty();
    assertThat(parameterDefinition.getIndex()).isNotNull().isEqualTo(0);
    assertThat(parameterDefinition.getExpanderClassName()).isNotEmpty();
  }

  @Test
  void expander_isOptional() {
    TargetMethodParameterDefinition parameterDefinition = TargetMethodParameterDefinition.builder()
        .name("parameter")
        .index(0)
        .build();
    assertThat(parameterDefinition.getName()).isNotEmpty();
    assertThat(parameterDefinition.getIndex()).isNotNull().isEqualTo(0);
    assertThat(parameterDefinition.getExpanderClassName()).isNull();
  }

  @Test
  void name_isRequired() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> TargetMethodParameterDefinition.builder().build());
  }

  @Test
  void index_isRequired() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> TargetMethodParameterDefinition.builder()
            .name("name")
            .build());
  }

  @Test
  void index_mustBePositive() {
    assertThatIllegalStateException()
        .isThrownBy(() -> TargetMethodParameterDefinition.builder()
            .name("name")
            .index(-1)
            .build());
  }

  @Test
  void equals_name_isNotCaseSensitive() {
    assertThat(TargetMethodParameterDefinition.builder()
        .name("param")
        .index(0)
        .build()).isEqualTo(
        TargetMethodParameterDefinition.builder()
            .name("PARAM")
            .index(0)
            .build());
  }

  @Test
  void equals_itself() {
    TargetMethodParameterDefinition parameterDefinition = TargetMethodParameterDefinition.builder()
        .name("param")
        .index(0)
        .build();
    assertThat(parameterDefinition).isEqualTo(parameterDefinition);
  }

  @Test
  void notEquals_name_caseInsensitive() {
    assertThat(TargetMethodParameterDefinition.builder()
        .name("param")
        .index(0)
        .build()).isNotEqualTo(
        TargetMethodParameterDefinition.builder()
            .name("name")
            .index(0)
            .build());
  }

  @Test
  void notEqual_toOtherTypes() {
    assertThat(TargetMethodParameterDefinition.builder()
        .name("param")
        .index(0)
        .build()).isNotEqualTo("A String");
  }

  @Test
  void toString_isNotEmpty() {
    assertThat(TargetMethodParameterDefinition.builder()
        .name("param")
        .index(0)
        .build()
        .toString()).isNotEmpty();
  }
}