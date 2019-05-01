package feign;

import feign.http.RequestSpecification;
import java.util.function.Consumer;

public interface RequestInterceptor extends Consumer<RequestSpecification> {

}
