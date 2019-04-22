package feign;

public interface TargetMethod {

  <R> R execute(Object[] args);

}
