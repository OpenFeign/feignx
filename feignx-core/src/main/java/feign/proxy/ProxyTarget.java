package feign.proxy;

import feign.FeignConfiguration;
import feign.Target;
import feign.TargetMethodHandler;
import feign.TargetMethodHandlerFactory;
import feign.http.RequestSpecification;
import feign.TargetMethodDefinition;
import feign.impl.TypeDrivenMethodHandlerFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A JDK Proxy {@link Target} implementation backed by an existing {@link Target} delegate.
 *
 * <p>
 * Uses reflection to look over the {@link Target} delegate, registering any methods that have been
 * identified by a {@link feign.Contract}.  Methods not registered bypass the proxy and are executed
 * on the original object.
 * </p>
 */
public class ProxyTarget<T> implements InvocationHandler, Target<T> {

  private static final String EQUALS = "equals";
  private static final String HASH_CODE = "hashCode";
  private static final String TO_STRING = "toString";

  private Target<T> delegate;
  private TargetMethodHandlerFactory methodHandlerFactory = new TypeDrivenMethodHandlerFactory();
  private Map<Method, TargetMethodHandler> methodHandlerMap = new LinkedHashMap<>();
  private final FeignConfiguration configuration;

  /**
   * Creates a new {@link ProxyTarget}.
   *
   * @param methods for this proxy to manage.
   * @param configuration for this instance.
   */
  public ProxyTarget(
      Collection<TargetMethodDefinition> methods, FeignConfiguration configuration) {
    this.delegate = configuration.getTarget();
    this.configuration = configuration;
    this.buildMethodHandlerMap(delegate, methods);
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
    TargetMethodHandler methodHandler = null;
    if (this.methodHandlerMap.containsKey(method)) {
      /* look for a method handler registered */
      methodHandler = this.methodHandlerMap.get(method);
    } else {
      /* default, static and non-annotated methods will not be in the map */
      if (method.isDefault()) {
        /* create the handler */
        methodHandler = this.createGuardMethodHandler(method, this);

        /* add it to the map for later use */
        this.methodHandlerMap.put(method, methodHandler);
      }
    }

    if (methodHandler != null) {
      /* execute the handler */
      return methodHandler.execute(args);
    } else {
      /* in our case, this means that the method is not implemented as we don't have a
       * handler for it. */
      throw new UnsupportedOperationException(
          "HttpMethod [" + method.getName() + "] is not supported by this implementation.");
    }
  }

  @Override
  public void apply(RequestSpecification requestSpecification) {
    this.delegate.apply(requestSpecification);
  }

  private void buildMethodHandlerMap(Target<?> target, Collection<TargetMethodDefinition> metadata) {
    Method[] methods = target.type().getMethods();

    /* loop through the methods and map them to the appropriate method handler */
    for (Method method : methods) {
      metadata.stream().filter(
          targetMethodMetadata -> method.getName().equalsIgnoreCase(
              targetMethodMetadata.getName()))
          .findFirst()
          .ifPresent(targetMethodMetadata -> {
            TargetMethodHandler methodHandler =
                methodHandlerFactory.create(targetMethodMetadata, configuration);
            methodHandlerMap.put(method, methodHandler);
          });
    }
  }

  private TargetMethodHandler createGuardMethodHandler(Method method, Object proxy) {
    TargetMethodHandler methodHandler;

    /* create a new Guard HttpMethod Handler and register it to the map */
    methodHandler = new GuardMethodHandler(method, this)
        .bind(proxy);

    return methodHandler;
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
