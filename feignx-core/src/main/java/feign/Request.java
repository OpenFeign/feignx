package feign;

import feign.http.HttpHeader;
import feign.http.HttpMethod;
import feign.http.RequestOptions;
import java.net.URI;

public interface Request {

  URI uri();

  byte[] content();

  HttpMethod method();

  HttpHeader[] headers();

  RequestOptions options();
}
