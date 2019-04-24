package feign.proxy;

import feign.Target;
import feign.support.Assert;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A JDK Proxy {@link Target} implementation backed by an existing {@link Target} delegate.
 *
 * <p>
 *   Uses reflection to look over the {@link Target} delegate, registering any methods
 *   that have been identified by a {@link feign.Contract}.  Methods not registered bypass
 *   the proxy and are executed on the original object.
 * </p>
 */
public class ProxyTarget<T> implements InvocationHandler, Target<T> {

  private static final String EQUALS = "equals";
  private static final String HASH_CODE = "hashCode";
  private static final String TO_STRING = "toString";

  private Target<T> delegate;
  private Map<Method, ProxyMethodHandler> methodHandlerMap = new LinkedHashMap<>();

  /**
   * Creates a new {@link ProxyTarget}.
   *
   * @param delegate to wrap.
   */
  public ProxyTarget(Target<T> delegate) {
    Assert.isNotNull(delegate, "delegate is required.");
    this.delegate = delegate;
  }

  @Override
  public Class<T> type() {
    return this.delegate.type();
  }

  @Override
  public String name() {
    return this.delegate.name();
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

    /* handle core methods */
    if (EQUALS.equals(method.getName())) {
      return this.equals(args[0]);
    } else if (HASH_CODE.equals(method.getName())) {
      return this.hashCode();
    } else if (TO_STRING.equals(method.getName())) {
      return this.toString();
    }

    /* only proxy methods that have been registered */
    ProxyMethodHandler methodHandler = null;
    if (this.methodHandlerMap.containsKey(method)) {
      /* look for a method handler registered */
      methodHandler = this.methodHandlerMap.get(method);
    } else {
      if (method.isDefault()) {
        /* create a new Guard Method Handler and register it to the map */
        GuardMethodHandler guardMethodHandler = new GuardMethodHandler(method, this);

        /* bind it to the proxy */
        guardMethodHandler.bind(proxy);

        /* add it to the map for later use */
        this.methodHandlerMap.put(method, guardMethodHandler);
        methodHandler = guardMethodHandler;
      }
    }

    if (methodHandler != null) {
      /* execute the handler */
      return methodHandler.execute(args);
    } else {
      /* in our case, this means that the method is not implemented as we don't have a
       * handler for it. */
      throw new UnsupportedOperationException(
          "Method [" + method.getName() + "] is not supported by this implementation.");
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (!Target.class.isAssignableFrom(obj.getClass())) {
      return false;
    }

    return this.delegate.equals(obj);
  }

  @Override
  public int hashCode() {
    return this.delegate.hashCode();
  }

  @Override
  public String toString() {
    return this.delegate.toString();
  }

}
