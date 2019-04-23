package feign.http;

public interface Response {

  Status status();

  Headers headers();

  Object body();
}
