package feign;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

public class FeignTests {

  @Test
  public void createTarget() {

    GitHub gitHub = Feign.builder()
        .target(GitHub.class, "https://api.github.com");

    assertThat(gitHub).isNotNull();
  }

  interface GitHub {

    List<Repository> getRepositories(String username);

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
