package feign.proxy;

import feign.TargetMethodHandler;
import feign.Target;
import feign.support.Assert;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Target Method Handler implementation for {@code default} or Guard method.
 * <p>
 *   This class uses certain parts of the JDK reflection API that may be considered unsafe.
 *   In JDK 9+, this type of access frowned upon and may be explicitly disabled in any
 *   JDK 11+.  Until a more complete solution appears, we will continue to use this approach.
 * </p>
 */
public class GuardMethodHandler implements TargetMethodHandler {

  private final MethodHandle guardMethodHandle;
  private boolean bound = false;

  /**
   * Creates a new Guard Method Handler.
   *
   * @param method to proxy.
   * @param target instance this method is for.
   */
  @SuppressWarnings("JavaReflectionMemberAccess")
  public GuardMethodHandler(Method method, Target<?> target) {
    Assert.isNotNull(method, "method is required.");
    Assert.isNotNull(target, "target is required.");
    try {
      /* attempt to create a new instance of the target type */
      Class<?> targetType = target.type();
      Constructor<Lookup> constructor = Lookup.class.getConstructor(Class.class);

      /* this is the line that breaks on JDK 9+, it violates the new security rules */
      constructor.setAccessible(true);

      /* create a temporary instance of the target and execute the method */
      this.guardMethodHandle = constructor.newInstance(targetType)
          .in(targetType)
          .unreflectSpecial(method, targetType);
    } catch (InstantiationException | InvocationTargetException | NoSuchMethodException
        | IllegalAccessException ie) {
      /* either the type does not expose a type that can be instantiated or
       * access to the type has been explicitly denied
       */
      throw new IllegalStateException(ie);
    }
  }

  /**
   * Bind the Method Handler to the proxy.
   *
   * @param proxy to bind to.
   */
  GuardMethodHandler bind(Object proxy) {
    this.guardMethodHandle.bindTo(proxy);
    this.bound = true;
    return this;
  }

  /**
   * If this Method Handler has been bound to a proxy.
   *
   * @return {@literal true} if this method handler is already bound, {@literal false} otherwise.
   */
  boolean isBound() {
    return this.bound;
  }

  /**
   * Execute the Method Handler.
   *
   * @param args for the method.
   * @return the result of the method.
   * @throws Throwable in the event of any exceptions during execution.
   */
  @Override
  public Object execute(Object[] args) throws Throwable {
    return this.guardMethodHandle.invokeWithArguments(args);
  }
}
