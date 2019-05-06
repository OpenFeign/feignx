package feign.template;

import feign.support.Assert;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Chunk that represents an Expression that, adheres to RFC 6570, and will be resolved during
 * expansion.
 */
public abstract class Expression implements Chunk {

  private static final String MULTIPLE_VALUE_DELIMITER = ",";
  private final Set<String> variables = new LinkedHashSet<>();
  private int limit;

  /**
   * Creates a new Expression.
   *
   * @param variableSpecification template.
   */
  Expression(String variableSpecification) {
    Assert.isNotEmpty(variableSpecification, "variable is required.");

    /* remove the leading and trailing braces if necessary */
    if (variableSpecification.startsWith("{")) {
      variableSpecification = variableSpecification
          .substring(1, variableSpecification.length() - 1);
    }

    if (variableSpecification.contains(MULTIPLE_VALUE_DELIMITER)) {
      /* multiple variables are present in the spec */
      String[] variableSpecifications = variableSpecification.split(MULTIPLE_VALUE_DELIMITER);
      this.variables.addAll(Arrays.asList(variableSpecifications));
    } else {
      this.variables.add(variableSpecification);
    }
    this.limit = -1;
  }

  /**
   * Creates a new Expression, with a prefix limiting the amount of characters to include during
   * expansion.
   *
   * @param variables template.
   * @param limit regular variables.
   */
  Expression(String variables, int limit) {
    this(variables);
    this.limit = limit;
  }

  /**
   * Expand this variables based on the value provided.
   *
   * @param variables to expand.
   * @return the expanded Expression value.
   */
  String expand(Map<String, ?> variables) {
    StringBuilder expanded = new StringBuilder();
    for (String variable : this.variables) {
      if (variables.containsKey(variable)) {
        String result = this.expandInternal(variables.get(variable));
        if (result != null) {

          /* trim the result to the limit if present */
          if (this.limit > 0) {
            result = result.substring(0, limit);
          }

          /* append the list delimiter based on this expression type when appending additional
           * values  */
          if (expanded.length() != 0) {
            expanded.append(",");
          }
          expanded.append(result);
        }
      }
    }
    return expanded.toString();
  }

  /**
   * Expand this variables based on the value provided.
   *
   * @param value to expand.
   * @return the expanded Expression value.
   */
  protected abstract String expandInternal(Object value);

  /**
   * Variable name for this expression.
   *
   * @return expression variables.
   */
  public Collection<String> getVariables() {
    return Collections.unmodifiableSet(this.variables);
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
      return "{" + this.variables + ":" + this.limit + "}";
    }
    return "{" + this.variables + "}";
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
    return variables.equals(that.variables);
  }

  @Override
  public int hashCode() {
    return Objects.hash(variables);
  }

  @Override
  public String toString() {
    return "Expression [" + "variables='" + variables + "'" + ", limit=" + limit + "]";
  }
}
