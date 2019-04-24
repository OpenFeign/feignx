package feign.http;

public interface Client {

  Response request(Request request) throws HttpException;
}
