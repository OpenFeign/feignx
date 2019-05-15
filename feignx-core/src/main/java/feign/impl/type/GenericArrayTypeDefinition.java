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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

/**
 * Type Definition for a {@link GenericArrayType}, a ParameterizedType with an Array supplied
 * as the Type Variable.
 */
public class GenericArrayTypeDefinition implements GenericArrayType, TypeDefinition {

  private final TypeDefinition genericComponentType;

  /**
   * Creates a new GenericArrayTypeDefiniton.
   *
   * @param genericComponentType containing the Type to define.
   */
  GenericArrayTypeDefinition(TypeDefinition genericComponentType) {
    this.genericComponentType = genericComponentType;
  }

  @Override
  public Class<?> getType() {
    return this.genericComponentType.getType();
  }

  @Override
  public Type getGenericComponentType() {
    return this.genericComponentType;
  }
}
