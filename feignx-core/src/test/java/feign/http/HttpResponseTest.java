package feign.http;

import static org.assertj.core.api.Assertions.assertThat;

import feign.Response;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

  /* response buffer */
  private static final byte[] BUFFER = new byte[1024];
  private static final Random RANDOM = new Random();

  @BeforeAll
  static void loadBuffer() {
    RANDOM.nextBytes(BUFFER);
  }

  @Test
  void responseByteArray_canBeReadMoreThanOnce() throws Exception {
    try (Response response = HttpResponse.builder()
        .body(new ByteArrayInputStream(BUFFER))
        .contentLength(1024)
        .status(200)
        .reason("OK")
        .build()) {
      /* read the response */
      byte[] data = response.toByteArray();
      assertThat(data).isNotNull().isNotEmpty();

      /* read it again, it should be the same */
      byte[] dup = response.toByteArray();
      assertThat(dup).isNotNull().isNotEmpty().isEqualTo(data);
    }
  }

  @Test
  void responseInputStream_cannotBeReadMoreThanOnce() throws Exception {
    try (Response response = HttpResponse.builder()
        .body(new ByteArrayInputStream(BUFFER))
        .contentLength(1024)
        .status(200)
        .reason("OK")
        .build()) {
      /* read it, using the input stream */
      try (BufferedInputStream bufferedInputStream = new BufferedInputStream(response.body())) {
        byte[] data = new byte[response.contentLength()];
        int read = bufferedInputStream.read(data, 0, response.contentLength());
        assertThat(read).isEqualTo(response.contentLength());
        assertThat(data).isNotNull().isNotEmpty();
      }

      /* read it again, should be at the end of the stream. */
      try (BufferedInputStream bufferedInputStream = new BufferedInputStream(response.body())) {
        byte[] data = new byte[response.contentLength()];
        int read = bufferedInputStream.read(data, 0, response.contentLength());
        assertThat(read).isEqualTo(-1);
        assertThat(data).isNotNull().containsOnly(0);
      }
    }
  }

  @Test
  void responseInputStream_isEqualToByteArray_ifByteArrayAlreadyCalled() throws Exception {
    try (Response response = HttpResponse.builder()
        .body(new ByteArrayInputStream(BUFFER))
        .contentLength(1024)
        .status(200)
        .reason("OK")
        .build()) {
      /* read the response */
      byte[] data = response.toByteArray();
      assertThat(data).isNotNull().isNotEmpty();

      /* read it again, it should be the same */
      try (BufferedInputStream bufferedInputStream = new BufferedInputStream(response.body())) {
        byte[] dup = new byte[response.contentLength()];
        int read = bufferedInputStream.read(dup, 0, response.contentLength());
        assertThat(read).isEqualTo(1024);
        assertThat(dup).isNotNull().isEqualTo(data);
      }
    }
  }

}