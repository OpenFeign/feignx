package feign.http;

import java.util.function.Consumer;

@FunctionalInterface
public interface RequestEncoder extends Consumer<RequestSpecification> {

}
