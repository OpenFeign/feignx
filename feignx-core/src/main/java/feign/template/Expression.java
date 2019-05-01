package feign.template;

import feign.support.Assert;
import java.util.Objects;

/**
 * Chunk that represents an Expression that, adheres to RFC 6570, and will be resolved
 * during expansion.
 */
public abstract class Expression implements Chunk {

  private final String variable;
  private int limit;

  /**
   * Creates a new Expression.
   *
   * @param variable template.
   */
  protected Expression(String variable) {
    Assert.isNotEmpty(variable, "variable is required.");
    this.variable = variable;
    this.limit = -1;
  }

  /**
   * Creates a new Expression, with a prefix limiting the amount of characters to include
   * during expansion.
   *
   * @param variable template.
   * @param limit regular variable.
   */
  protected Expression(String variable, int limit) {
    this(variable);
    this.limit = limit;
  }

  /**
   * Expand this variable based on the value provided.
   *
   * @param value to expand.
   * @return the expanded Expression value.
   */
  public String expand(Object value) {
    String result = this.expandInternal(value);

    /* honor the limit, if present */
    return (this.limit > 0) ? result.substring(0, limit) : result;
  }

  /**
   * Expand this variable based on the value provided.
   *
   * @param value to expand.
   * @return the expanded Expression value.
   */
  protected abstract String expandInternal(Object value);

  /**
   * Variable name for this expression.
   *
   * @return expression variable.
   */
  public String getVariable() {
    return this.variable;
  }

  /**
   * The number of characters to limit the expanded value to.
   *
   * @return the expanded character limit.
   */
  public int getLimit() {
    return limit;
  }

  @Override
  public String getValue() {
    if (this.limit > 0) {
      return "{" + this.variable + ":" + this.limit + "}";
    }
    return "{" + this.variable + "}";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Expression)) {
      return false;
    }
    Expression that = (Expression) obj;
    return variable.equals(that.variable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(variable);
  }

  @Override
  public String toString() {
    return "Expression [" + "variable='" + variable + "'" + ", limit=" + limit + "]";
  }
}
