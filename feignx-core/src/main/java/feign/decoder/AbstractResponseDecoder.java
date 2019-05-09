package feign.decoder;

import feign.Response;
import feign.ResponseDecoder;
import feign.exception.FeignException;
import java.io.InputStream;

/**
 * Base Class for Response Decoders.
 */
public abstract class AbstractResponseDecoder implements ResponseDecoder {

  /**
   * Decode the Response.  Provides support for handling common response types.
   *
   * @param response to decode.
   * @param type desired.
   * @param <T> defining the type desired.
   * @return an instance of the desired type.
   */
  @SuppressWarnings("unchecked")
  @Override
  public <T> T decode(Response response, Class<T> type) {
    try {
      if (byte[].class.equals(type)) {
        return (T) response.toByteArray();
      } else if (InputStream.class.isAssignableFrom(type)) {
        /* return a byte array input stream */
        return (T) response.body();
      }

      /* no body */
      if (response == null || response.body() == null) {
        return null;
      }

      /* dispatch to the sub classes */
      return this.decodeInternal(response, type);
    } catch (Exception ex) {
      throw new FeignException("Error decoding the Response", ex,
          this.getClass().getName() + "#decode");
    }
  }

  /**
   * Decode the Response.
   *
   * @param response to decode.
   * @param type desired.
   * @param <T> defining the type desired
   * @return an instance of the desired type.
   */
  protected abstract <T> T decodeInternal(Response response, Class<T> type);
}
