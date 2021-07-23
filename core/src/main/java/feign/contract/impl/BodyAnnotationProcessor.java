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

import feign.contract.Body;
import feign.contract.ParameterAnnotationProcessor;
import feign.contract.TargetMethodDefinition.Builder;

/**
 * Annotation Processor for the {@link Body} annotation. Registers which method parameters contains
 * the request body.
 */
public class BodyAnnotationProcessor implements ParameterAnnotationProcessor<Body> {

  @Override
  public void process(Body annotation, String name, Integer index, String type, Builder builder) {
    builder.body(index);
  }
}
