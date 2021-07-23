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

package feign.contract;

import java.lang.annotation.Annotation;

/**
 * Interface for Annotation Processors responsible for dealing with annotated method parameters.
 * Used in both reflective and compile time modes.
 *
 * @param <T> supported annotation.
 */
public interface ParameterAnnotationProcessor<T extends Annotation> {

  /**
   * Evaluate the annotation against the provided parameter information.  Implementations are
   * expected to update the builder via side-effects.
   *
   * @param annotation to evaluate.
   * @param name       of the parameter.
   * @param index      of the parameter in the method.
   * @param type       fully qualified class name of the parameter type.
   * @param builder    with the current method context.
   */
  void process(T annotation, String name, Integer index, String type,
      TargetMethodDefinition.Builder builder);
}
