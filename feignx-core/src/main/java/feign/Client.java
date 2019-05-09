package feign;

import feign.http.HttpException;

public interface Client {

  Response request(Request request) throws HttpException;
}
