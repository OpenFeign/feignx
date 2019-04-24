package feign.template;

import java.util.Map;

public interface TemplateParameter {

  String name();

  default boolean encode() {
    return true;
  }
}
