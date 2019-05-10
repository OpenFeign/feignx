package feign.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class HttpExceptionTest {

  @Test
  void canCreate() {
    HttpException exception = new HttpException("Request Failed", new IOException());
    assertThat(exception).isNotNull();
  }

  @Test
  void canCreate_withRequest() {
    HttpException exception =
        new HttpException("Request Failed", new IOException(),
            new HttpRequest());
    assertThat(exception.getRequest()).isNotEmpty();
    assertThat(exception.getResponse()).isEmpty();
  }

  @Test
  void canCreate_withRequestAndResponse() {
    HttpException exception =
        new HttpException("Request Failed", new IOException(),
            new HttpRequest(),
            new HttpResponse());
    assertThat(exception.getRequest()).isNotEmpty();
    assertThat(exception.getResponse()).isNotEmpty();
  }
}