package feign.template;

/**
 * Represents a {@link UriTemplate} parameter to be resolved.
 */
public interface TemplateParameter {

  /**
   * Name of the parameter.  Matches up with the variable name defined in an Expression.
   *
   * @return the parameter name.
   */
  String name();

}
