package feign.template;

import feign.support.Assert;
import java.util.Objects;

/**
 * Template Parameter implementation that acts as a simple value object.
 */
public class SimpleTemplateParameter implements TemplateParameter {

  private final String name;

  /**
   * Creates a new SimpleTemplateParameter.
   *
   * @param name of the parameter.
   * @throws IllegalArgumentException if the name is {@literal null} or empty.
   */
  public SimpleTemplateParameter(String name) {
    Assert.isNotEmpty(name, "name is required");
    this.name = name;
  }

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof SimpleTemplateParameter)) {
      return false;
    }
    SimpleTemplateParameter that = (SimpleTemplateParameter) obj;
    return name.toLowerCase().equals(that.name.toLowerCase());
  }

  @Override
  public int hashCode() {
    return Objects.hash(name.toLowerCase());
  }
}
