package feign;

/**
 * A component that can parse a given Response and "decode" the Response body into the
 * type desired.
 */
@FunctionalInterface
public interface ResponseDecoder {

  /**
   * Decode the Response Body into the desired type.
   *
   * @param response to decode.
   * @param type instance desired.
   * @param <T> desired type.
   * @return an instance of the desired type.
   * @throws feign.exception.FeignException if the response could not be decoded.
   */
  <T> T decode(Response response, Class<T> type);
}
