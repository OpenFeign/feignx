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

package feign.impl.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Type Definition for a {@link ParameterizedType}, the simplest type of Generic.
 */
public class ParameterizedTypeDefinition implements ParameterizedType, TypeDefinition {

  private final Type owner;
  private final Type raw;
  private final Type[] arguments;

  /**
   * Creates a new ParameterizedTypeDefinition.
   *
   * @param owner of the type, in the event this ParameterizedType is contained within another.
   * @param raw type contained.
   * @param arguments for each TypeVariable in the type definition.
   */
  ParameterizedTypeDefinition(Type owner, Type raw, Type... arguments) {
    this.owner = owner;
    this.raw = raw;
    this.arguments = arguments.clone();
  }

  @Override
  public Class<?> getType() {
    return (Class<?>) this.getRawType();
  }

  @Override
  public Type[] getActualTypeArguments() {
    return arguments.clone();
  }

  @Override
  public Type getRawType() {
    return this.raw;
  }

  @Override
  public Type getOwnerType() {
    return this.owner;
  }
}
