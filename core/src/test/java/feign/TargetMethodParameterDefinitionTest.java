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

import feign.contract.TargetMethodParameterDefinition;
import feign.template.ExpressionExpander;
import feign.template.expander.DefaultExpander;
import java.util.List;
import org.junit.jupiter.api.Test;

class TargetMethodParameterDefinitionTest {

  @Test
  void create_withExpander() {
    TargetMethodParameterDefinition parameterDefinition = TargetMethodParameterDefinition.builder()
        .name("parameter")
        .index(0)
        .type(String.class.getName())
        .expanderClassName("io.openfeign.expander.StringExpander")
        .build();
    assertThat(parameterDefinition.getName()).isNotEmpty();
    assertThat(parameterDefinition.getIndex()).isNotNull().isEqualTo(0);
    assertThat(parameterDefinition.getExpanderClassName()).isNotEmpty();
    assertThat(parameterDefinition.getType()).isNotEmpty();
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
            .expanderClassName(DefaultExpander.class.getName())
            .type(String.class.getName())
            .build());
  }

  @Test
  void index_mustBePositive() {
    assertThatIllegalStateException()
        .isThrownBy(() -> TargetMethodParameterDefinition.builder()
            .name("name")
            .index(-1)
            .expanderClassName(DefaultExpander.class.getName())
            .type(String.class.getName())
            .build());
  }

  @Test
  void type_isRequired() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> TargetMethodParameterDefinition.builder()
        .name("name")
        .index(0)
        .expanderClassName(DefaultExpander.class.getName())
        .build());
  }

  @Test
  void expanderClass_isRequired() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> TargetMethodParameterDefinition.builder()
            .name("name")
            .index(0)
            .type(String.class.getName())
            .build());
  }

  @Test
  void equals_name_isNotCaseSensitive() {
    assertThat(TargetMethodParameterDefinition.builder()
        .name("param")
        .index(0)
        .type(String.class.getName())
        .expanderClassName(DefaultExpander.class.getName())
        .build()).isEqualTo(
        TargetMethodParameterDefinition.builder()
            .name("PARAM")
            .index(0)
            .type(String.class.getName())
            .expanderClassName(DefaultExpander.class.getName())
            .build());
  }

  @Test
  void equals_itself() {
    TargetMethodParameterDefinition parameterDefinition = TargetMethodParameterDefinition.builder()
        .name("param")
        .index(0)
        .type(String.class.getName())
        .expanderClassName(DefaultExpander.class.getName())
        .build();
    assertThat(parameterDefinition).isEqualTo(parameterDefinition);
  }

  @Test
  void notEquals_name_caseInsensitive() {
    assertThat(TargetMethodParameterDefinition.builder()
        .name("param")
        .index(0)
        .type(String.class.getName())
        .expanderClassName(DefaultExpander.class.getName())
        .build()).isNotEqualTo(
        TargetMethodParameterDefinition.builder()
            .name("another")
            .index(0)
            .type(String.class.getName())
            .expanderClassName(DefaultExpander.class.getName())
            .build());
  }

  @Test
  void notEquals_index() {
    assertThat(TargetMethodParameterDefinition.builder()
        .name("param")
        .index(0)
        .type(String.class.getName())
        .expanderClassName(DefaultExpander.class.getName())
        .build()).isNotEqualTo(
        TargetMethodParameterDefinition.builder()
            .name("param")
            .index(1)
            .type(String.class.getName())
            .expanderClassName(DefaultExpander.class.getName())
            .build());
  }

  @Test
  void notEquals_type() {
    assertThat(TargetMethodParameterDefinition.builder()
        .name("param")
        .index(0)
        .type(String.class.getName())
        .expanderClassName(DefaultExpander.class.getName())
        .build()).isNotEqualTo(
        TargetMethodParameterDefinition.builder()
            .name("param")
            .index(0)
            .type(List.class.getName())
            .expanderClassName(DefaultExpander.class.getName())
            .build());
  }

  @Test
  void notEquals_expander() {
    assertThat(TargetMethodParameterDefinition.builder()
        .name("param")
        .index(0)
        .type(String.class.getName())
        .expanderClassName(DefaultExpander.class.getName())
        .build()).isNotEqualTo(
        TargetMethodParameterDefinition.builder()
            .name("param")
            .index(0)
            .type(List.class.getName())
            .expanderClassName(ExpressionExpander.class.getName())
            .build());
  }

  @Test
  void notEqual_toOtherTypes() {
    assertThat(TargetMethodParameterDefinition.builder()
        .name("param")
        .index(0)
        .type(String.class.getName())
        .expanderClassName(DefaultExpander.class.getName())
        .build()).isNotEqualTo("A String");
  }

  @Test
  void toString_isNotEmpty() {
    assertThat(TargetMethodParameterDefinition.builder()
        .name("param")
        .index(0)
        .type(String.class.getName())
        .expanderClassName(DefaultExpander.class.getName())
        .build()
        .toString()).isNotEmpty();
  }

  @Test
  void hashCode_isNotEmpty() {
    assertThat(TargetMethodParameterDefinition.builder()
        .name("param")
        .index(0)
        .type(String.class.getName())
        .expanderClassName(DefaultExpander.class.getName())
        .build()
        .hashCode()).isNotNull();
  }
}
