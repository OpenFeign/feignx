/*
 * Copyright 2019-2020 OpenFeign Contributors
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

import java.lang.reflect.Type;

/**
 * Generic Type Definition.
 */
public interface TypeDefinition extends Type {

  /**
   * The Class type defining this Type.
   *
   * @return class reference.
   */
  Class<?> getType();

  /**
   * Return the actual type for this definition.  For basic types, this will be the same as
   * {@link #getType()}, but for parameterized types that are collections or containers, this
   * will return the type contained.
   *
   * @return the actual type reference.
   */
  Class<?> getActualType();

  /**
   * Returns whether this definition is considered a Collection, which is basically anything
   * that extends {@link Iterable}.  The expectation here is that, if a type is collection like,
   * then {@link #getType()} will return an {@link Iterable} type.
   *
   * @return if this definition is for a Collection.
   */
  boolean isCollectionLike();

  /**
   * Returns whether this definition is one of a select type of objects that act as containers.
   * Container types act as vehicles to decorate the actual types.  The expectation here is that
   * {@link #getActualType()} will return the contained type.  Some examples are
   * {@link java.util.Optional} and {@link java.util.concurrent.Future}.
   *
   * @return if this definition represents a contained type.
   */
  boolean isContainer();

}
