package feign;

public interface TargetMethodHandler {

  Object execute(Object[] args) throws Throwable;

}
