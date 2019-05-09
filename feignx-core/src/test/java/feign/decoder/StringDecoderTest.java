package feign.decoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import feign.ResponseDecoder;
import feign.exception.FeignException;
import feign.http.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StringDecoderTest {

  private ResponseDecoder decoder = new StringDecoder();

  @Mock
  private Response response;

  @Test
  void feignException_whenTypeIsNotString() {
    when(response.body()).thenReturn(new ByteArrayInputStream("content".getBytes()));
    assertThrows(FeignException.class, () -> decoder.decode(response, List.class));
  }

  @Test
  void ioException_whenResponseCouldNotBeRead() throws Exception {
    when(response.body()).thenReturn(new ByteArrayInputStream("content".getBytes()));
    when(this.response.toByteArray()).thenThrow(new IOException("IO Exception"));
    assertThrows(FeignException.class, () -> decoder.decode(response, String.class));
  }

  @Test
  void decodeResponse_fromByteArray() throws IOException {
    when(response.body()).thenReturn(new ByteArrayInputStream("content".getBytes()));
    when(this.response.toByteArray()).thenReturn("content".getBytes(StandardCharsets.UTF_8));
    String result = this.decoder.decode(response, String.class);
    assertThat(result).isEqualTo("content");
  }

}