package feign.target;

import feign.Target;
import feign.support.Assert;
import java.util.Objects;

public abstract class AbstractTarget<T> implements Target<T> {

  private final Class<T> type;
  private final String name;

  protected AbstractTarget(Class<T> type) {
    Assert.isNotNull(type, "type is required.");
    this.type = type;
    this.name = type.getSimpleName();
  }

  protected AbstractTarget(Class<T> type, String name) {
    Assert.isNotNull(type, "type is required.");
    Assert.isNotEmpty(name, "name is required.");
    this.type = type;
    this.name = name;
  }

  @Override
  public Class<T> type() {
    return this.type;
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
    if (!(obj instanceof AbstractTarget)) {
      return false;
    }
    AbstractTarget<?> that = (AbstractTarget<?>) obj;
    return type.equals(that.type) &&
        name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, name);
  }

  @Override
  public String toString() {
    return "AbstractTarget [" + "type=" + type + ", name='" + name + "']";
  }
}
