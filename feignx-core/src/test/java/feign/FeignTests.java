package feign;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import java.util.List;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class FeignTests {

  @Test
  void createTarget() {
    GitHub gitHub = Feign.builder()
        .target(GitHub.class, "https://api.github.com");
    assertThat(gitHub).isNotNull();
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

      public Repository() {
        super();
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
