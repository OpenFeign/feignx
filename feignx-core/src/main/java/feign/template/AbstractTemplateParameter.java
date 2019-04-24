package feign.template;

import feign.support.Assert;

public abstract class AbstractTemplateParameter implements TemplateParameter {

  private final String name;
  private final boolean encode;

  protected AbstractTemplateParameter(String name, boolean encode) {
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
}
