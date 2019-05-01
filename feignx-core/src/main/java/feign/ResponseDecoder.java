package feign;

import feign.http.Response;

@FunctionalInterface
public interface ResponseDecoder {

  <T> T decode(Response response, Class<T> type);
}
