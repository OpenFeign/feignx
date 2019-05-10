package feign.encoder;

import feign.RequestEncoder;
import feign.http.RequestSpecification;
import java.nio.charset.StandardCharsets;

/**
 * HttpRequest Encoder that encodes the request content as a String.  This implementation uses
 * the {@code toString()} method on the content object to encode the data.
 */
public class StringEncoder implements RequestEncoder {

  @Override
  public void apply(Object content, RequestSpecification requestSpecification) {
    if (content != null) {
      /* use the content objects toString() method to populate the request */
      requestSpecification.content(content.toString().getBytes(StandardCharsets.UTF_8));
    }
  }
}
