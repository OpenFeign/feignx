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
import static org.junit.jupiter.api.Assertions.assertThrows;

import feign.support.Assert;
import feign.template.ExpressionExpander;
import feign.template.ExpressionVariable;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CachingExpanderRegistryTest {

  private CachingExpanderRegistry expanderRegistry = new CachingExpanderRegistry();

  @Test
  void map_shouldBeExpandedWith_MapExpander() {
    assertThat(this.expanderRegistry.getExpanderByType(Map.class)).isInstanceOf(MapExpander.class);
  }

  @Test
  void list_shouldBeExpandedWith_ListExpander() {
    assertThat(this.expanderRegistry.getExpanderByType(List.class))
        .isInstanceOf(ListExpander.class);
  }

  @Test
  void simpleTypes_shouldBeExpandedWith_SimpleExpander() {
    assertThat(this.expanderRegistry.getExpanderByType(String.class))
        .isInstanceOf(SimpleExpander.class);
  }

  @Test
  void expanderInstances_shouldBeReused() {
    /* this should register Strings with the SimpleExpander */
    ExpressionExpander expander = expanderRegistry.getExpanderByType(String.class);

    /* retrieve it again and it should be the same instance */
    assertThat(expander).isEqualTo(expanderRegistry.getExpanderByType(String.class));
  }

  @Test
  void customExpanderTypes_shouldBeInstantiated() {
    ExpressionExpander expander = this.expanderRegistry.getExpander(CustomExpander.class);
    assertThat(expander).isNotNull().isInstanceOf(CustomExpander.class);
  }

  @Test
  void customExpanderTypes_shouldBeReused() {
    ExpressionExpander expander = this.expanderRegistry.getExpander(CustomExpander.class);
    assertThat(expander).isEqualTo(this.expanderRegistry.getExpander(CustomExpander.class));
  }

  @Test
  void customExpanderTypes_mustHaveDefaultConstructor() {
    assertThrows(IllegalStateException.class,
        () -> this.expanderRegistry.getExpander(InvalidExpander.class));
  }

  static class CustomExpander implements ExpressionExpander {

    @Override
    public String expand(ExpressionVariable variable, Object value) {
      return null;
    }
  }

  static class InvalidExpander implements ExpressionExpander {

    public InvalidExpander(String name) {
      super();
      Assert.isNotEmpty(name, "can't be empty");
    }

    @Override
    public String expand(ExpressionVariable variable, Object value) {
      return null;
    }
  }
}
