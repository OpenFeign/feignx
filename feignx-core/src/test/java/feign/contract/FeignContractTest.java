package feign.contract;

import static org.assertj.core.api.Assertions.assertThat;

import feign.Contract;
import feign.TargetMethodDefinition;
import feign.http.HttpMethod;
import feign.impl.UriTarget;
import feign.template.SimpleTemplateParameter;
import java.util.Collection;
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
            && targetMethodDefinition.getBody() == -1);
    assertThat(methodDefinitions).anyMatch(
        targetMethodDefinition -> targetMethodDefinition.getName().equalsIgnoreCase("post")
            && targetMethodDefinition.getReturnType() == String.class
            && targetMethodDefinition.getMethod() == HttpMethod.POST
            && targetMethodDefinition.getTemplateParameters().contains(
            new SimpleTemplateParameter("parameter"))
            && targetMethodDefinition.getBody() == 1);
    assertThat(methodDefinitions).anyMatch(
        targetMethodDefinition -> targetMethodDefinition.getName().equalsIgnoreCase("put")
            && targetMethodDefinition.getReturnType() == String.class
            && targetMethodDefinition.getMethod() == HttpMethod.PUT
            && targetMethodDefinition.getTemplateParameters()
            .contains(new SimpleTemplateParameter("parameter"))
            && targetMethodDefinition.getBody() == 1);
    assertThat(methodDefinitions).anyMatch(
        targetMethodDefinition -> targetMethodDefinition.getName().equalsIgnoreCase("patch")
            && targetMethodDefinition.getReturnType() == String.class
            && targetMethodDefinition.getMethod() == HttpMethod.PATCH
            && targetMethodDefinition.getTemplateParameters()
            .contains(new SimpleTemplateParameter("parameter"))
            && targetMethodDefinition.getBody() == 1);
    assertThat(methodDefinitions).anyMatch(
        targetMethodDefinition -> targetMethodDefinition.getName().equalsIgnoreCase("delete")
            && targetMethodDefinition.getReturnType() == void.class
            && targetMethodDefinition.getMethod() == HttpMethod.DELETE
            && targetMethodDefinition.getTemplateParameters()
            .contains(new SimpleTemplateParameter("parameter"))
            && targetMethodDefinition.getBody() == -1);
  }

  @Test
  void replaceUri_whenDefinedAsAbsolute() {
    Contract contract = new FeignContract();
    Collection<TargetMethodDefinition> methodDefinitions =
        contract.apply(new UriTarget<>(AbsoluteRequests.class, "https://www.example.com"));

    /* the search method should not have the uri root */
    TargetMethodDefinition methodDefinition = methodDefinitions.stream()
        .filter(
            targetMethodDefinition -> targetMethodDefinition.getName().equalsIgnoreCase("search"))
        .findAny()
        .get();
    assertThat(methodDefinition.getUri()).isEqualTo("https://www.google.com?q={query}");

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

    @Request(value = "/update", method = HttpMethod.PATCH)
    String patch(@Param("parameter") String value, @Body String content);

    @Request(value = "/remove", method = HttpMethod.DELETE)
    void delete(@Param("parameter") String value);
  }

  @Request("/resources")
  interface AbsoluteRequests {

    @Request("/")
    String get();

    @Request("https://www.google.com?q={query}")
    String search(@Param("query") String query);

  }

}