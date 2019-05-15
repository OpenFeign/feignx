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

package feign;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

class TargetMethodDefinitionTest {

  @Test
  void getUri_alwaysReturnsAValue() {
    TargetMethodDefinition targetMethodDefinition = new TargetMethodDefinition(mock(Target.class));
    assertThat(targetMethodDefinition.getUri()).isNotNull();
  }

  @Test
  void equals_name_caseSensitive() {
    TargetMethodDefinition targetMethodDefinition = new TargetMethodDefinition(mock(Target.class));
    targetMethodDefinition.name("name");

    TargetMethodDefinition anotherDefinition = new TargetMethodDefinition(mock(Target.class));
    anotherDefinition.name("name");
    assertThat(targetMethodDefinition).isEqualTo(anotherDefinition);
  }

  @Test
  void notEqual_name_caseSensitive() {
    TargetMethodDefinition targetMethodDefinition = new TargetMethodDefinition(mock(Target.class));
    targetMethodDefinition.name("Name");

    TargetMethodDefinition anotherDefinition = new TargetMethodDefinition(mock(Target.class));
    anotherDefinition.name("name");
    assertThat(targetMethodDefinition).isNotEqualTo(anotherDefinition);
  }
}