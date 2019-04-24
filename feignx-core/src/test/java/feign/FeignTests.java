package feign;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import feign.contract.Header;
import feign.contract.PathParam;
import feign.contract.Request;
import feign.http.Method;
import java.util.List;
import org.junit.jupiter.api.Test;

class FeignTests {

  @Test
  void createTarget() {
    GitHub gitHub = Feign.builder()
        .target(GitHub.class, "https://api.github.com");
    assertThat(gitHub).isNotNull();
  }

  @Test
  void throwUnsupportedOperationException_whenMethodIsNotRegistered() {
    GitHub gitHub = Feign.builder()
        .target(GitHub.class, "https://api.github.com");

    /* the get repositories method is not annotated, thus not registered */
    assertThrows(UnsupportedOperationException.class,
        () -> gitHub.getRepositories("username"));

  }

  interface GitHub {

    @Request(value = "/repos/{owner}",
        headers = {@Header(name = "Accept", value = "application/json")})
    List<Repository> getRepositories(@PathParam("owner") String owner);

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
