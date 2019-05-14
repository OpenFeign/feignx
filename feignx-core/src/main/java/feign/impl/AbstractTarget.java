package feign.impl;

import feign.Target;
import feign.support.Assert;
import java.util.Objects;

/**
 * Base implementation of all Targets.
 *
 * @param <T> type for this target.
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbstractTarget<T> implements Target<T> {

  private final Class<T> type;
  private final String name;

  /**
   * Creates a new Target.
   *
   * @param type of the Target.
   */
  protected AbstractTarget(Class<T> type) {
    Assert.isNotNull(type, "type is required.");
    this.type = type;
    this.name = type.getSimpleName();
  }

  /**
   * Creates a new Target.
   *
   * @param type of the Target.
   * @param name of the Target.
   */
  protected AbstractTarget(Class<T> type, String name) {
    Assert.isNotNull(type, "type is required.");
    Assert.isNotEmpty(name, "name is required.");
    this.type = type;
    this.name = name;
  }

  /**
   * Target Type.
   *
   * @return the target type.
   */
  @Override
  public Class<T> type() {
    return this.type;
  }

  /**
   * Name of this Target.
   *
   * @return the target name.
   */
  @Override
  public String name() {
    return this.name;
  }

  /**
   * Determines if the provided Target is equal to this instance.  Targets are considered equal when
   * the contained type and match exactly.
   *
   * @param obj to compare.
   * @return {@literal true} if the provided object is equal to this instance, {@literal false}
   *        otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof AbstractTarget)) {
      return false;
    }
    AbstractTarget<?> that = (AbstractTarget<?>) obj;
    return type.equals(that.type)
        && name.equals(that.name);
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
