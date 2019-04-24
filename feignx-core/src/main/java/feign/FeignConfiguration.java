package feign;

import feign.http.Client;
import feign.http.RequestEncoder;
import feign.http.ResponseDecoder;

public interface FeignConfiguration {

  Client getClient();

  RequestEncoder getRequestEncoder();

  ResponseDecoder getResponseDecoder();

  Contract getContract();

  <T> Target<T> getTarget();
}
