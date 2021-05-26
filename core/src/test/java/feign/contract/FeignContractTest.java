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

package feign.contract;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import feign.Contract;
import feign.FeignConfiguration;
import feign.RequestOptions;
import feign.Response;
import feign.http.HttpMethod;
import feign.impl.AbsoluteUriTarget;
import feign.template.expander.DefaultExpander;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class FeignContractTest {

  @Test
  void can_parseSimpleInterface() {
    Contract contract = new FeignContract();
    FeignConfiguration configuration = mock(FeignConfiguration.class);
    when(configuration.getTarget()).thenReturn(new AbsoluteUriTarget("http://localhost"));

    TargetDefinition targetDefinition = contract.apply(SimpleInterface.class, configuration);
    Collection<TargetMethodDefinition> methodDefinitions = targetDefinition.getMethodDefinitions();

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
            && targetMethodDefinition.getReturnType().getType() == String.class
            && targetMethodDefinition.getMethod() == HttpMethod.GET
            && targetMethodDefinition.getParameterDefinitions().isEmpty()
            && targetMethodDefinition.getBody() == -1
            && targetMethodDefinition.getConnectTimeout() == RequestOptions.DEFAULT_CONNECT_TIMEOUT
            && targetMethodDefinition.getReadTimeout() == RequestOptions.DEFAULT_READ_TIMEOUT
            && targetMethodDefinition.isFollowRedirects());

    /* implicit body parameter */
    assertThat(methodDefinitions).anyMatch(
        targetMethodDefinition -> targetMethodDefinition.getName().equalsIgnoreCase("post")
            && targetMethodDefinition.getReturnType().getType() == String.class
            && targetMethodDefinition.getMethod() == HttpMethod.POST
            && targetMethodDefinition.getParameterDefinitions().contains(
            TargetMethodParameterDefinition.builder()
                .name("parameter")
                .index(0)
                .type(String.class.getCanonicalName())
                .expanderClassName(DefaultExpander.class.getName())
                .build())
            && targetMethodDefinition.getBody() == 1);

    /* explicit body parameter */
    assertThat(methodDefinitions).anyMatch(
        targetMethodDefinition -> targetMethodDefinition.getName().equalsIgnoreCase("put")
            && targetMethodDefinition.getReturnType().getType() == String.class
            && targetMethodDefinition.getMethod() == HttpMethod.PUT
            && targetMethodDefinition.getParameterDefinitions()
            .contains(TargetMethodParameterDefinition.builder()
                .name("parameter")
                .index(0)
                .type(String.class.getCanonicalName())
                .expanderClassName(DefaultExpander.class.getName())
                .build())
            && targetMethodDefinition.getBody() == 1);

    /* void return type */
    assertThat(methodDefinitions).anyMatch(
        targetMethodDefinition -> targetMethodDefinition.getName().equalsIgnoreCase("delete")
            && targetMethodDefinition.getReturnType().getType() == void.class
            && targetMethodDefinition.getMethod() == HttpMethod.DELETE
            && targetMethodDefinition.getParameterDefinitions()
            .contains(TargetMethodParameterDefinition.builder()
                .name("parameter")
                .index(0)
                .type(String.class.getCanonicalName())
                .expanderClassName(DefaultExpander.class.getName())
                .build())
            && targetMethodDefinition.getBody() == -1);

    /* request options and generic return type */
    assertThat(methodDefinitions).anyMatch(
        targetMethodDefinition -> targetMethodDefinition.getName().equalsIgnoreCase("search")
            && targetMethodDefinition.getReturnType().getType() == List.class
            && targetMethodDefinition.getMethod() == HttpMethod.GET
            && targetMethodDefinition.getParameterDefinitions().isEmpty()
            && targetMethodDefinition.getBody() == -1
            && targetMethodDefinition.getConnectTimeout() == 1000
            && targetMethodDefinition.getReadTimeout() == 2000
            && !targetMethodDefinition.isFollowRedirects());

    /* map parameter type */
    assertThat(methodDefinitions).anySatisfy(targetMethodDefinition -> {
      boolean properties = targetMethodDefinition.getName().equalsIgnoreCase("map")
          && targetMethodDefinition.getReturnType().getType() == List.class
          && targetMethodDefinition.getMethod() == HttpMethod.GET
          && targetMethodDefinition.getBody() == -1;
      assertThat(properties).isTrue();

      targetMethodDefinition.getParameterDefinition(0)
          .ifPresent(
              parameter -> assertThat(parameter.getType())
                  .isEqualToIgnoringCase(Map.class.getCanonicalName()));
    });

    /* list parameter type */
    assertThat(methodDefinitions).anySatisfy(
        targetMethodDefinition -> {
          boolean properties = targetMethodDefinition.getName().equalsIgnoreCase("list")
              && targetMethodDefinition.getReturnType().getType() == List.class
              && targetMethodDefinition.getMethod() == HttpMethod.GET
              && targetMethodDefinition.getBody() == -1;
          assertThat(properties).isTrue();

          targetMethodDefinition.getParameterDefinition(0)
              .ifPresent(
                  parameter -> assertThat(parameter.getType())
                      .isEqualToIgnoringCase(List.class.getCanonicalName()));
        });

    /* response return type */
    assertThat(methodDefinitions).anyMatch(
        targetMethodDefinition -> targetMethodDefinition.getName().equalsIgnoreCase("response")
            && targetMethodDefinition.getReturnType().getType() == Response.class
            && targetMethodDefinition.getMethod() == HttpMethod.GET
            && targetMethodDefinition.getParameterDefinitions()
            .contains(TargetMethodParameterDefinition.builder()
                .name("parameters")
                .index(0)
                .type(String.class.getCanonicalName())
                .expanderClassName(DefaultExpander.class.getName())
                .build())
            && targetMethodDefinition.getBody() == -1);
  }

  @Test
  void replaceUri_whenDefinedAsAbsolute() {
    Contract contract = new FeignContract();
    FeignConfiguration configuration = mock(FeignConfiguration.class);
    when(configuration.getTarget()).thenReturn(new AbsoluteUriTarget("http://localhost"));

    TargetDefinition definition = contract.apply(AbsoluteRequests.class, configuration);
    Collection<TargetMethodDefinition> methodDefinitions = definition.getMethodDefinitions();

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

  @SuppressWarnings("unused")
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

    @Request("/map")
    List<String> map(@Param("parameters") Map<String, String> parameters);

    @Request("/list")
    List<String> list(@Param("parameters") List<String> parameters);

    @Request(value = "/response")
    Response response(@Param("parameters") String parameters);
  }

  @SuppressWarnings("unused")
  @Request("/resources")
  interface AbsoluteRequests {

    @Request("/")
    String get();

    @Request("https://www.google.com?q={query}")
    String search(@Param("query") String query);

  }

}