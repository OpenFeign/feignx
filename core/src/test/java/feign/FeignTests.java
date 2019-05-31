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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import feign.exception.FeignException;
import feign.http.HttpException;
import feign.http.client.UrlConnectionClient;
import feign.logging.SimpleLogger;
import feign.retry.ConditionalRetry;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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

    mockServerClient.when(
        request()
            .withMethod("GET")
            .withPath("/users/openfeign/repos/feign/contributors")
    ).respond(
        response()
            .withStatusCode(503));

    mockServerClient.when(
        request()
            .withMethod("GET")
            .withPath("/search/repositories")
            .withQueryStringParameter("sort", "stars")
            .withQueryStringParameter("q", "topic:ruby+topic:rails")
            .withQueryStringParameter("order", "desc")
    ).respond(
        response()
            .withStatusCode(200)
            .withBody("[{\"name\":\"feign\"},{\"name\":\"feignx\"}]"));
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
  void createTarget_andExecute_withMapParameters() {
    GitHub gitHub = Feign.builder()
        .decoder(new GitHubDecoder())
        .target(GitHub.class, "http://localhost:9999");
    Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put("q", "topic:ruby+topic:rails");
    parameters.put("sort", "stars");
    parameters.put("order", "desc");
    List<Repository> repositories = gitHub.searchRepositories(parameters);
    assertThat(repositories).isNotNull().isNotEmpty();
    assertThat(repositories.get(0).name).contains("feignx");
  }

  @Test
  void createTarget_andExecute_withRetry_withException() {
    Client client = spy(new UrlConnectionClient());

    Logger logger = SimpleLogger.builder()
        .setEnabled(true)
        .setHeadersEnabled(true)
        .setRequestEnabled(true)
        .setResponseEnabled(true).build();

    Retry retry = ConditionalRetry.builder(3, logger, new RethrowExceptionHandler())
        .interval(10, TimeUnit.MILLISECONDS)
        .multiplier(2)
        .exception(HttpException.class)
        .build();

    GitHub gitHub = Feign.builder()
        .logger(logger)
        .retry(retry)
        .client(client)
        .decoder(new GitHubDecoder())
        .target(GitHub.class, "http://localhost:9999");

    assertThrows(FeignException.class, () -> gitHub.getContributors("openfeign", "feign"));

    /* verify that the retry occurred */
    verify(client, times(3)).request(any(feign.Request.class));
  }

  @Test
  void createTarget_andExecute_withRetry_withCustomException() {
    Client client = spy(new UrlConnectionClient());

    Logger logger = SimpleLogger.builder()
        .setEnabled(true)
        .setHeadersEnabled(true)
        .setRequestEnabled(true)
        .setResponseEnabled(true).build();

    ExceptionHandler exceptionHandler = new ExceptionHandler() {
      @Override
      public RuntimeException apply(Throwable throwable) {
        /* convert the throwable into a business exception */
        return new BusinessException(throwable);
      }
    };

    Retry retry = ConditionalRetry.builder(3, logger, exceptionHandler)
        .interval(10, TimeUnit.MILLISECONDS)
        .multiplier(2)
        .exception(BusinessException.class)
        .build();

    GitHub gitHub = Feign.builder()
        .logger(logger)
        .retry(retry)
        .client(client)
        .exceptionHandler(exceptionHandler)
        .decoder(new GitHubDecoder())
        .target(GitHub.class, "http://localhost:9999");

    assertThrows(BusinessException.class, () -> gitHub.getContributors("openfeign", "feign"));

    /* verify that the retry occurred */
    verify(client, times(3)).request(any(feign.Request.class));
  }

  @Test
  void create_withConfiguration() {
    ExceptionHandler exceptionHandler = new RethrowExceptionHandler();

    Logger logger = SimpleLogger.builder()
        .setEnabled(true)
        .setHeadersEnabled(true)
        .setRequestEnabled(true)
        .setResponseEnabled(true).build();

    Retry retry = ConditionalRetry.builder(3, logger, exceptionHandler)
        .interval(10, TimeUnit.MILLISECONDS)
        .multiplier(2)
        .useRetryAfter()
        .exception(IOException.class)
        .statusCode(503)
        .statusCode(504)
        .build();

    GitHub gitHub = Feign.builder()
        .client(new UrlConnectionClient())
        .contract(new FeignContract())
        .encoder(new StringEncoder())
        .decoder(new StringDecoder())
        .exceptionHandler(exceptionHandler)
        .interceptor(requestSpecification -> System.out.println("intercept"))
        .executor(Executors.newSingleThreadExecutor())
        .logger(logger)
        .retry(retry)
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
        gitHub::createPullRequest);
  }


  interface GitHub {

    @Request("/users/{owner}/repos")
    @Headers({@Header(name = "Accept", value = "application/json")})
    List<Repository> getRepositories(@Param("owner") String owner);

    @Request("/users/{owner}/repos")
    @Headers({@Header(name = "Accept", value = "application/json")})
    CompletableFuture<List<Repository>> getRepositoriesAsync(@Param("owner") String owner);

    @Request("/users/{owner}/repos/{repo}/contributors")
    List<Contributor> getContributors(
        @Param("owner") String owner, @Param("repo") String repository);

    @Request("/search/repositories{?parameters*}")
    List<Repository> searchRepositories(@Param("parameters") Map<String, String> parameters);

    void createPullRequest();

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

    class Contributor {

      String name;

      Contributor(String name) {
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

  static class BusinessException extends RuntimeException {

    BusinessException(Throwable cause) {
      super(cause);
    }
  }

}
