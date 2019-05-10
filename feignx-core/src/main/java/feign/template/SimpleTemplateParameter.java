package feign.template;

import feign.support.Assert;
import java.util.Objects;

/**
 * Template Parameter implementation that acts as a simple value object.
 */
public class SimpleTemplateParameter implements TemplateParameter {

  private final String name;
  private final boolean encode;

  /**
   * Creates a new SimpleTemplateParameter.
   *
   * @param name of the parameter.
   * @throws IllegalArgumentException if the name is {@literal null} or empty.
   */
  public SimpleTemplateParameter(String name) {
    Assert.isNotEmpty(name, "name is required");
    this.name = name;
    this.encode = false;
  }

  /**
   * Creates a new SimpleTemplateParameter.
   *
   * @param name of the parameter.
   * @param encode flag that controls if this parameter should be pct-encoded when expanded.
   * @throws IllegalArgumentException if the name is {@literal null} or empty.
   */
  public SimpleTemplateParameter(String name, boolean encode) {
    Assert.isNotEmpty(name, "name is required");
    this.name = name;
    this.encode = encode;
  }

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public boolean encode() {
    return this.encode;
  }

  @Override
  public boolean equals(Object pbj) {
    if (this == pbj) {
      return true;
    }
    if (!(pbj instanceof SimpleTemplateParameter)) {
      return false;
    }
    SimpleTemplateParameter that = (SimpleTemplateParameter) pbj;
    return name.toLowerCase().equals(that.name.toLowerCase());
  }

  @Override
  public int hashCode() {
    return Objects.hash(name.toLowerCase());
  }
}
