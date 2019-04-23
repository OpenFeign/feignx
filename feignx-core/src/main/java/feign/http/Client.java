package feign.http;

import feign.http.Request;
import feign.http.Response;

@FunctionalInterface
public interface Client {

  Response execute(Request request);
}
