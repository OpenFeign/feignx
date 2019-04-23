package feign.http;

public interface RequestSpecification {

  RequestSpecification uri(String uri);

  RequestSpecification header(String name, String value);

  RequestSpecification parameter(String name, String value);

  RequestSpecification contentType();

  RequestSpecification content(Object content);

  Request build();

}
