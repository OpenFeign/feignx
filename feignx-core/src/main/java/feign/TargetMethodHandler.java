package feign;

/**
 * Functional Handler for a method found on a given Target.
 */
public interface TargetMethodHandler {

  /**
   * Handle the method execution.
   *
   * @param args to apply.
   * @return the result of the operation.
   * @throws Throwable if the operation failed.
   */
  Object execute(Object[] args) throws Throwable;

}
