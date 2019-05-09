package feign;

import feign.http.RequestSpecification;
import java.util.function.Consumer;

/**
 * Consumer that can be used to modify a Request before being processed.
 */
public interface RequestInterceptor extends Consumer<RequestSpecification> {

}
