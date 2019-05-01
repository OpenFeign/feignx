package feign.template;

public interface TemplateParameter {

  String name();

  default boolean encode() {
    return true;
  }
}
