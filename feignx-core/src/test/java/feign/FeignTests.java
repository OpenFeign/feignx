package feign;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import feign.FeignTests.GitHub.Repository;
import feign.contract.Header;
import feign.contract.Headers;
import feign.contract.Param;
import feign.contract.Request;
import java.util.List;
import org.junit.jupiter.api.Test;

class FeignTests {

  @Test
  void createTarget_andExecuteRequest() {
    GitHub gitHub = Feign.builder()
        .target(GitHub.class, "https://api.github.com");
    assertThat(gitHub).isNotNull();

    String repositories = gitHub.getRepositories("openfeign");
    assertThat(repositories).isNotEmpty();
  }

  @Test
  void throwUnsupportedOperationException_whenMethodIsNotRegistered() {
    GitHub gitHub = Feign.builder()
        .target(GitHub.class, "https://api.github.com");

    /* the get repositories method is not annotated, thus not registered */
    assertThrows(UnsupportedOperationException.class,
        gitHub::getContributors);

  }

  @Request("https://api.github.com")
  interface GitHub {

    @Request("/users/{owner}/repos")
    @Headers({@Header(name = "Accept", value = "application/json")})
    String getRepositories(@Param("owner") String owner);

    List<String> getContributors();

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
