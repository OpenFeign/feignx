package feign.proxy;

public interface ProxyMethodHandler {

  Object execute(Object[] args) throws Throwable;

}
