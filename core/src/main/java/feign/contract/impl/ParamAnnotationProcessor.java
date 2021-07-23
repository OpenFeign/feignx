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

package feign.contract.impl;

import feign.contract.Param;
import feign.contract.ParameterAnnotationProcessor;
import feign.contract.TargetMethodDefinition.Builder;
import feign.contract.TargetMethodParameterDefinition;
import feign.template.ExpressionExpander;

/**
 * Annotation Processor for the {@link Param} annotation.  Registers the parameter, along with
 * it's name, type and index in the method, with the method definition.
 */
public class ParamAnnotationProcessor implements ParameterAnnotationProcessor<Param> {

  @Override
  public void process(Param annotation, String name, Integer index, String type, Builder builder) {
    Class<? extends ExpressionExpander> expanderClass = annotation.expander();
    String expanderClassName = expanderClass.getName();

    builder.parameterDefinition(
        index, TargetMethodParameterDefinition.builder()
            .name(annotation.value())
            .index(index)
            .type(type)
            .expanderClassName(expanderClassName)
            .build());
  }
}
