/*
 * Copyright 2019 OpenFeign Contributors
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

import feign.Target;
import feign.TargetMethodHandler;
import feign.support.Assert;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Target HttpMethod Handler implementation for {@code default} or Guard method.
 * <p>
 *   This class uses certain parts of the JDK reflection API that may be considered unsafe.
 *   In JDK 9+, this type of access frowned upon and may be explicitly disabled in any
 *   JDK 11+.  Until a more complete solution appears, we will continue to use this approach.
 * </p>
 */
public class GuardMethodHandler implements TargetMethodHandler {

  private final MethodHandle guardMethodHandle;

  /**
   * Creates a new Guard HttpMethod Handler.
   *
   * @param method to proxy.
   * @param target instance this method is for.
   * @param proxy to bind this handler to.
   */
  GuardMethodHandler(Method method, Target<?> target, Object proxy) {
    Assert.isNotNull(method, "method is required.");
    Assert.isNotNull(target, "target is required.");
    try {
      /* attempt to create a new instance of the target type */
      Class<?> targetType = target.type();
      Constructor<Lookup> constructor = Lookup.class.getDeclaredConstructor(Class.class);

      /* this is the line that breaks on JDK 9+, it violates the new security rules */
      constructor.setAccessible(true);

      /* create a temporary instance of the target and execute the method */
      this.guardMethodHandle = constructor.newInstance(targetType)
          .in(targetType)
          .unreflectSpecial(method, targetType)
          .bindTo(proxy);
    } catch (InstantiationException | InvocationTargetException | NoSuchMethodException
        | IllegalAccessException ie) {
      /* either the type does not expose a type that can be instantiated or
       * access to the type has been explicitly denied
       */
      throw new IllegalStateException(ie);
    }
  }

  /**
   * Execute the HttpMethod Handler.
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
