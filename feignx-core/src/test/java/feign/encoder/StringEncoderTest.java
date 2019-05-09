package feign.encoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import feign.RequestEncoder;
import feign.http.RequestSpecification;
import org.junit.jupiter.api.Test;

class StringEncoderTest {

  @Test
  void apply_toString() {
    RequestEncoder encoder = new StringEncoder();
    RequestSpecification requestSpecification = mock(RequestSpecification.class);
    encoder.apply("content", requestSpecification);
    verify(requestSpecification, times(1)).content(any(byte[].class));
  }

  @Test
  void apply_withNullSkips() {
    RequestEncoder encoder = new StringEncoder();
    RequestSpecification requestSpecification = mock(RequestSpecification.class);
    encoder.apply(null, requestSpecification);
    verifyZeroInteractions(requestSpecification);
  }
}