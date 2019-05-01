package feign.http.decoder;

import feign.exception.FeignException;
import feign.http.Response;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Response Decoder that reads the response buffer and converts it to a
 * UTF-8 String.
 */
public class StringDecoder extends AbstractResponseDecoder {

  @SuppressWarnings("unchecked")
  @Override
  protected <T> T decodeInternal(Response response, Class<T> type) {
    if (String.class.equals(type)) {
      try {
        /* ready the body into a string */
        return (T) new String(response.toByteArray(), StandardCharsets.UTF_8);
      } catch (IOException ioe) {
        throw new IllegalStateException("Error occurred reading response: " + ioe, ioe);
      }
    }
    throw new IllegalArgumentException("Error occurred while decoding the Response, "
        + "type: " + type.getSimpleName() + " is not supported by this decoder.");
  }
}
