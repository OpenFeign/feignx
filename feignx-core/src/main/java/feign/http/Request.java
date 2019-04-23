package feign.http;

import java.net.URI;

public interface Request {

  URI uri();

  Object content();

  Method method();

  Headers headers();

}
