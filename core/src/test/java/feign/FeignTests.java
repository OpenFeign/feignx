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

package feign;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import feign.ExceptionHandler.RethrowExceptionHandler;
import feign.FeignTests.GitHub.Repository;
import feign.contract.FeignContract;
import feign.contract.Header;
import feign.contract.Headers;
import feign.contract.Param;
import feign.contract.Request;
import feign.decoder.StringDecoder;
import feign.encoder.StringEncoder;
import feign.http.client.UrlConnectionClient;
import feign.logging.SimpleLogger;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;

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
    Logger logger = SimpleLogger.builder()
        .setEnabled(true)
        .setHeadersEnabled(true)
        .setRequestEnabled(true)
        .setResponseEnabled(true).build();

    GitHub gitHub = Feign.builder()
        .logger(logger)
        .decoder(new GitHubDecoder())
        .target(GitHub.class, "http://localhost:9999");
    assertThat(gitHub).isNotNull();

    List<Repository> repositories = gitHub.getRepositories("openfeign");
    assertThat(repositories).isNotEmpty();
  }

  @Test
  void createTarget_andExecuteAsync() throws Exception {
    Logger logger = SimpleLogger.builder()
        .setEnabled(true)
        .setHeadersEnabled(true)
        .setRequestEnabled(true)
        .setResponseEnabled(true).build();

    GitHub gitHub = Feign.builder()
        .logger(logger)
        .decoder(new GitHubDecoder())
        .executor(Executors.newFixedThreadPool(10))
        .target(GitHub.class, "http://localhost:9999");
    assertThat(gitHub).isNotNull();

    CompletableFuture<List<Repository>> result = gitHub.getRepositoriesAsync("openfeign");
    List<Repository> repositories = result.get();
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
        .logger(SimpleLogger.builder()
            .setEnabled(true)
            .setHeadersEnabled(true)
            .setRequestEnabled(true)
            .setResponseEnabled(true).build())
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

    @Request("/users/{owner}/repos")
    @Headers({@Header(name = "Accept", value = "application/json")})
    CompletableFuture<List<Repository>> getRepositoriesAsync(@Param("owner") String owner);

    void getContributors();

    default String getOwner() {
      return "owner";
    }

    class Repository {

      String name;

      Repository(String name) {
        super();
        this.name = name;
      }

    }
  }

  @SuppressWarnings("unchecked")
  static class GitHubDecoder implements ResponseDecoder {

    @Override
    public <T> T decode(Response response, Class<T> type) {
      try {
        String data = new String(response.toByteArray(), StandardCharsets.UTF_8);
        return (T) Collections.singletonList(new Repository(data));
      } catch (IOException ioe) {
        throw new RuntimeException(ioe.getMessage(), ioe);
      }
    }
  }

}
