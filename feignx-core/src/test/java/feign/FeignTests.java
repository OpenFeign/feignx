package feign;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import feign.FeignTests.GitHub.Repository;
import feign.contract.FeignContract;
import feign.contract.Header;
import feign.contract.Headers;
import feign.contract.Param;
import feign.contract.Request;
import feign.decoder.StringDecoder;
import feign.encoder.StringEncoder;
import feign.exception.ExceptionHandler.RethrowExceptionHandler;
import feign.http.RequestSpecification;
import feign.http.client.UrlConnectionClient;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.BodyWithContentType;

class FeignTests {

  private static ClientAndServer mockServerClient;

  @BeforeAll
  static void prepareServer() {
    mockServerClient = ClientAndServer.startClientAndServer(9999);

    mockServerClient.when(
        request()
            .withMethod("GET")
            .withPath("/users/openfeign/repos")
    ).respond(
        response()
            .withStatusCode(200)
            .withBody("{\"name\":\"feign\"}"));
  }

  @AfterAll
  static void shutdownServer() {
    mockServerClient.stop();
  }

  @Test
  void createTargetAndExecute() {
    GitHub gitHub = Feign.builder()
        .decoder(new ResponseDecoder() {
          @SuppressWarnings("unchecked")
          @Override
          public <T> T decode(Response response, Class<T> type) {
            return (T) Collections.singletonList(new Repository("openfeign"));
          }
        })
        .target(GitHub.class, "http://localhost:9999");
    assertThat(gitHub).isNotNull();

    List<Repository> repositories = gitHub.getRepositories("openfeign");
    assertThat(repositories).isNotEmpty();
  }

  @Test
  void create_withConfiguration() {
    GitHub gitHub = Feign.builder()
        .client(new UrlConnectionClient())
        .contract(new FeignContract())
        .encoder(new StringEncoder())
        .decoder(new StringDecoder())
        .exceptionHandler(new RethrowExceptionHandler())
        .interceptor(requestSpecification -> System.out.println("intercept"))
        .executor(Executors.newSingleThreadExecutor())
        .target(GitHub.class, "https://api.github.com");
    assertThat(gitHub).isNotNull();
  }

  @Test
  void defaultMethods_areNotManaged() {
    GitHub gitHub = Feign.builder()
        .target(GitHub.class, "https://api.github.com");
    String owner = gitHub.getOwner();
    assertThat(owner).isEqualTo("owner");
  }

  @Test
  void throwUnsupportedOperationException_whenMethodIsNotRegisteredAndNotDefault() {
    GitHub gitHub = Feign.builder()
        .target(GitHub.class, "https://api.github.com");

    /* the get repositories method is not annotated, thus not registered */
    assertThrows(UnsupportedOperationException.class,
        gitHub::getContributors);
  }


  interface GitHub {

    @Request("/users/{owner}/repos")
    @Headers({@Header(name = "Accept", value = "application/json")})
    List<Repository> getRepositories(@Param("owner") String owner);

    List<String> getContributors();

    default String getOwner() {
      return "owner";
    }

    class Repository {

      private String name;

      Repository(String name) {
        super();
        this.name = name;
      }

      public String getName() {
        return name;
      }

      public void setName(String name) {
        this.name = name;
      }
    }
  }

}
