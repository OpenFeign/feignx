package feign.template;

import feign.support.Assert;
import java.util.Objects;

public class SimpleTemplateParameter implements TemplateParameter {

  private final String name;
  private final boolean encode;

  public SimpleTemplateParameter(String name) {
    Assert.isNotEmpty(name, "name is required");
    this.name = name;
    this.encode = false;
  }

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
    return name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
