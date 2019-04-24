package feign.http;

import java.util.function.Consumer;

public interface RequestInterceptor extends Consumer<RequestSpecification> {

}
