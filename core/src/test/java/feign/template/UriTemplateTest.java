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

package feign.template;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class UriTemplateTest {

  @Test
  void create_withLiterals() {
    UriTemplate template = UriTemplate.create("https://www.example.com/path?parameter=value");
    assertThat(template).isNotNull();
    assertThat(template.getExpressions()).isEmpty();
  }

  @Test
  void theKitchenSink() throws Exception {
    UriTemplate template =
        UriTemplate.create("{+scheme}www{.host*}{/path*}{;params}{?query*}{&cont*}{#fragment}");
    assertThat(template).isNotNull();
    assertThat(template.getExpressions()).isNotEmpty();

    Collection<Chunk> expressions = template.getExpressions();
    assertThat(expressions)
        .hasAtLeastOneElementOfType(SimpleExpression.class)
        .hasAtLeastOneElementOfType(ReservedExpression.class)
        .hasAtLeastOneElementOfType(DotExpression.class)
        .hasAtLeastOneElementOfType(PathSegmentExpression.class)
        .hasAtLeastOneElementOfType(PathStyleExpression.class)
        .hasAtLeastOneElementOfType(FormStyleExpression.class)
        .hasAtLeastOneElementOfType(FormContinuationStyleExpression.class)
        .hasAtLeastOneElementOfType(FragmentExpression.class);

    /* expand it */
    Map<String, Object> variables = new LinkedHashMap<>();
    variables.put("scheme", "https://");
    variables.put("host", Arrays.asList("example", "com"));
    variables.put("path", Arrays.asList("resources","items"));
    variables.put("params", Collections.singletonMap("filter", "name"));
    variables.put("query", Collections.singletonMap("sort", "descending"));
    variables.put("cont", Collections.singletonMap("page", "0"));
    variables.put("fragment", "total");

    URI result = template.expand(variables);
    assertThat(result).isNotNull();
    assertThat(result.toString())
        .isEqualTo("https://www.example.com/resources/items;params=filter,name?"
                + "sort=descending&page=0#total");
    assertThat(result.toURL()).isNotNull();
  }
}