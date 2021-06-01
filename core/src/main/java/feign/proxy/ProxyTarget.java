/*
 * Copyright 2019-2021 OpenFeign Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package feign.proxy;

import feign.FeignConfiguration;
import feign.Target;
import feign.TargetMethodHandler;
import feign.TargetMethodHandlerFactory;
import feign.contract.TargetDefinition;
import feign.impl.TypeDrivenMethodHandlerFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
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

  private final Class<T> targetType;
  private final TargetDefinition definition;
  private final TargetMethodHandlerFactory methodHandlerFactory;
  private final Map<Method, TargetMethodHandler> methodHandlerMap = new LinkedHashMap<>();

  /**
   * Creates a new {@link ProxyTarget}.
   *
   * @param definition    for this proxy to manage.
   * @param configuration for this instance.
   */
  ProxyTarget(TargetDefinition definition, FeignConfiguration configuration) {
    this(definition, new TypeDrivenMethodHandlerFactory(), configuration);
  }

  /**
   * Creates a new {@link ProxyTarget}.
   *
   * @param definition           for this proxy to manage.
   * @param methodHandlerFactory to use when creating method handlers.
   * @param configuration        for this instance.
   */
  @SuppressWarnings("unchecked")
  ProxyTarget(TargetDefinition definition,
      TargetMethodHandlerFactory methodHandlerFactory,
      FeignConfiguration configuration) {
    this.definition = definition;

    this.methodHandlerFactory = methodHandlerFactory;
    try {
      this.targetType = (Class<T>) Class
          .forName(definition.getFullyQualifiedTargetClassName());
    } catch (ClassNotFoundException cnfe) {
      throw new IllegalArgumentException("Target Type Class cannot be found", cnfe);
    }
    this.buildMethodHandlerMap(configuration);
  }

  /**
   * The Target Type, defer to the delegate.
   *
   * @return the target type.
   */
  @Override
  public Class<T> type() {
    return this.targetType;
  }

  /**
   * Name of this Target, defer to the delegate.
   *
   * @return the target name.
   */
  @Override
  public String name() {
    return this.targetType.getName();
  }

  /**
   * Invoke the desired method on the Proxy.
   *
   * @param proxy  object being invoked.
   * @param method being invoked.
   * @param args   for the method.
   * @return the result of the method invocation.
   * @throws Throwable if an error occurs during processing.
   */
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
        methodHandler = this.createGuardMethodHandler(method, proxy);

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

  /**
   * Creates a Map of Method to Method Handler based on the TargetMethodDefinitions provided.
   *
   * @param configuration to use when building method handlers.
   */
  private void buildMethodHandlerMap(FeignConfiguration configuration) {
    Method[] methods = this.targetType.getMethods();

    /* loop through the methods and map them to the appropriate method handler */
    for (Method method : methods) {
      this.definition.getMethodDefinitions().stream().filter(
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

  /**
   * Creates a new GuardMethodHandler, for default/guard method implementations.
   *
   * @param method with a default/guard implementation.
   * @param proxy  to bind the handler to.
   * @return a new TargetMethodHandler instance.
   */
  private TargetMethodHandler createGuardMethodHandler(Method method, Object proxy) {
    /* create a new Guard HttpMethod Handler and register it to the map */
    return new GuardMethodHandler(method, this, proxy);
  }

  /**
   * Determines if the provided object is equal to this target.  Since this is a JDK proxy, we
   * delegate to the proxied Target.
   *
   * @param obj to compare.
   * @return {@literal true} if the objects are equal, {@literal false} otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!Target.class.isAssignableFrom(obj.getClass())) {
      return false;
    }
    return this.definition.equals(obj);
  }

  /**
   * Determines the hash for this object.  Defers to the delegate.
   *
   * @return hash code.
   */
  @Override
  public int hashCode() {
    return this.definition.hashCode();
  }

  /**
   * String value.  Defers to the delegate.
   *
   * @return string representation of the delegate.
   */
  @Override
  public String toString() {
    return this.definition.toString();
  }

}
