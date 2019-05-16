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

package feign.contract;

import static org.assertj.core.api.Assertions.assertThat;

import feign.Contract;
import feign.TargetMethodDefinition;
import feign.http.HttpMethod;
import feign.RequestOptions;
import feign.impl.UriTarget;
import feign.template.SimpleTemplateParameter;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;

class FeignContractTest {

  @Test
  void can_parseSimpleInterface() {
    Contract contract = new FeignContract();
    Collection<TargetMethodDefinition> methodDefinitions = contract.apply(
        new UriTarget<>(SimpleInterface.class, "https://example.com"));

    assertThat(methodDefinitions).isNotEmpty();

    /* verify the defaults */
    assertThat(methodDefinitions).allMatch(
        targetMethodDefinition -> targetMethodDefinition.getUri().startsWith("/resources"));
    assertThat(methodDefinitions).allMatch(
        targetMethodDefinition -> targetMethodDefinition.getHeaders().stream().anyMatch(
            header -> header.name().equalsIgnoreCase("Accept")));

    /* verify each method is registered */
    assertThat(methodDefinitions).anyMatch(
        targetMethodDefinition -> targetMethodDefinition.getName().equalsIgnoreCase("get")
            && targetMethodDefinition.getReturnType() == String.class
            && targetMethodDefinition.getMethod() == HttpMethod.GET
            && targetMethodDefinition.getTemplateParameters().contains(
            new SimpleTemplateParameter("parameter"))
            && targetMethodDefinition.getBody() == -1
            && targetMethodDefinition.getConnectTimeout() == RequestOptions.DEFAULT_CONNECT_TIMEOUT
            && targetMethodDefinition.getReadTimeout() == RequestOptions.DEFAULT_READ_TIMEOUT
            && targetMethodDefinition.isFollowRedirects());

    /* implicit body parameter */
    assertThat(methodDefinitions).anyMatch(
        targetMethodDefinition -> targetMethodDefinition.getName().equalsIgnoreCase("post")
            && targetMethodDefinition.getReturnType() == String.class
            && targetMethodDefinition.getMethod() == HttpMethod.POST
            && targetMethodDefinition.getTemplateParameters().contains(
            new SimpleTemplateParameter("parameter"))
            && targetMethodDefinition.getBody() == 1);

    /* explicit body parameter */
    assertThat(methodDefinitions).anyMatch(
        targetMethodDefinition -> targetMethodDefinition.getName().equalsIgnoreCase("put")
            && targetMethodDefinition.getReturnType() == String.class
            && targetMethodDefinition.getMethod() == HttpMethod.PUT
            && targetMethodDefinition.getTemplateParameters()
            .contains(new SimpleTemplateParameter("parameter"))
            && targetMethodDefinition.getBody() == 1);

    /* void return type */
    assertThat(methodDefinitions).anyMatch(
        targetMethodDefinition -> targetMethodDefinition.getName().equalsIgnoreCase("delete")
            && targetMethodDefinition.getReturnType() == void.class
            && targetMethodDefinition.getMethod() == HttpMethod.DELETE
            && targetMethodDefinition.getTemplateParameters()
            .contains(new SimpleTemplateParameter("parameter"))
            && targetMethodDefinition.getBody() == -1);

    /* request options and generic return type */
    assertThat(methodDefinitions).anyMatch(
        targetMethodDefinition -> targetMethodDefinition.getName().equalsIgnoreCase("search")
            && targetMethodDefinition.getReturnType() == List.class
            && targetMethodDefinition.getMethod() == HttpMethod.GET
            && targetMethodDefinition.getBody() == -1
            && targetMethodDefinition.getConnectTimeout() == 1000
            && targetMethodDefinition.getReadTimeout() == 2000
            && !targetMethodDefinition.isFollowRedirects());
  }

  @Test
  void replaceUri_whenDefinedAsAbsolute() {
    Contract contract = new FeignContract();
    Collection<TargetMethodDefinition> methodDefinitions =
        contract.apply(new UriTarget<>(AbsoluteRequests.class, "https://www.example.com"));

    /* the search method should not have the uri root */
    //noinspection OptionalGetWithoutIsPresent
    TargetMethodDefinition methodDefinition = methodDefinitions.stream()
        .filter(
            targetMethodDefinition -> targetMethodDefinition.getName().equalsIgnoreCase("search"))
        .findAny()
        .get();
    assertThat(methodDefinition.getUri()).isEqualTo("https://www.google.com?q={query}");

    //noinspection OptionalGetWithoutIsPresent
    TargetMethodDefinition getDefinition = methodDefinitions.stream()
        .filter(
            targetMethodDefinition -> targetMethodDefinition.getName().equalsIgnoreCase("get"))
        .findAny()
        .get();
    assertThat(getDefinition.getUri()).isEqualTo("/resources/");
  }

  @Request("/resources")
  @Headers(value = @Header(name = "Accept", value = "application/json"))
  interface SimpleInterface {

    @Request(value = "/")
    String get();

    @Request(value = "/create", method = HttpMethod.POST)
    String post(@Param("parameter") String value, String content);

    @Request(value = "/replace", method = HttpMethod.PUT)
    String put(@Param("parameter") String value, @Body String content);

    @Request(value = "/remove", method = HttpMethod.DELETE)
    void delete(@Param("parameter") String value);

    @Request(value = "/search", method = HttpMethod.GET, followRedirects = false,
        readTimeout = 2000, connectTimeout = 1000)
    List<String> search();
  }

  @Request("/resources")
  interface AbsoluteRequests {

    @Request("/")
    String get();

    @Request("https://www.google.com?q={query}")
    String search(@Param("query") String query);

  }

}