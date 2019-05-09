package feign;

import java.util.Collection;

public interface Header {

  String name();

  Collection<String> values();
}
