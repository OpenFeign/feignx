package feign;

import feign.http.HttpException;
import feign.http.Request;
import feign.http.Response;

public interface Client {

  Response request(Request request) throws HttpException;
}
