package feign.http;

@FunctionalInterface
public interface ResponseDecoder {

  <T> T decode(Response response, Class<T> type);
}
