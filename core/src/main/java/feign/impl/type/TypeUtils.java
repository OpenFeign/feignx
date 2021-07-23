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

package feign.impl.type;

/**
 * Utility Class for working with Types and Class Name.
 */
public class TypeUtils {

  /**
   * Create a new Class instance for the class name provided.
   *
   * @param fullyQualifiedClassName to parse.
   * @return the class instance, if found.
   * @throws IllegalStateException if the class could not be found.
   */
  public static Class<?> getInstance(String fullyQualifiedClassName) {
    try {
      return Class.forName(fullyQualifiedClassName);
    } catch (ClassNotFoundException cnfe) {
      if (isPrimitiveType(fullyQualifiedClassName)) {
        return getPrimitiveInstance(fullyQualifiedClassName);
      }
      throw new IllegalStateException("Error occurred obtaining class instance.", cnfe);
    }
  }

  private static boolean isPrimitiveType(String type) {
    return Boolean.TYPE.getName().equalsIgnoreCase(type)
        || Character.TYPE.getName().equalsIgnoreCase(type)
        || Byte.TYPE.getName().equalsIgnoreCase(type)
        || Short.TYPE.getName().equalsIgnoreCase(type)
        || Integer.TYPE.getName().equalsIgnoreCase(type)
        || Long.TYPE.getName().equalsIgnoreCase(type)
        || Float.TYPE.getName().equalsIgnoreCase(type)
        || Double.TYPE.getName().equalsIgnoreCase(type)
        || Void.TYPE.getName().equalsIgnoreCase(type);
  }

  private static Class<?> getPrimitiveInstance(String type) {
    if (Boolean.TYPE.getName().equalsIgnoreCase(type)) {
      return Boolean.TYPE;
    } else if (Character.TYPE.getName().equalsIgnoreCase(type)) {
      return Character.TYPE;
    } else if (Byte.TYPE.getName().equalsIgnoreCase(type)) {
      return Byte.TYPE;
    } else if (Short.TYPE.getName().equalsIgnoreCase(type)) {
      return Short.TYPE;
    } else if (Integer.TYPE.getName().equalsIgnoreCase(type)) {
      return Integer.TYPE;
    } else if (Long.TYPE.getName().equalsIgnoreCase(type)) {
      return Long.TYPE;
    } else if (Float.TYPE.getName().equalsIgnoreCase(type)) {
      return Float.TYPE;
    } else if (Double.TYPE.getName().equalsIgnoreCase(type)) {
      return Double.TYPE;
    } else if (Void.TYPE.getName().equalsIgnoreCase(type)) {
      return Void.TYPE;
    }
    throw new IllegalArgumentException("Not a primitive type " + type);
  }

}
