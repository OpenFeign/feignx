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

import feign.contract.impl.BodyAnnotationProcessor;
import feign.contract.impl.HeadersAnnotationProcessor;
import feign.contract.impl.ParamAnnotationProcessor;
import feign.contract.impl.RequestAnnotationProcessor;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;

/**
 * Contract that uses Feign annotations.
 */
public class FeignContract extends AbstractAnnotationDrivenContract {

  /**
   * Creates a new Feign Contract.
   */
  public FeignContract() {
    super();
    this.registerAnnotationProcessor(Request.class, new RequestAnnotationProcessor());
    this.registerAnnotationProcessor(Headers.class, new HeadersAnnotationProcessor());
    this.registerParameterAnnotationProcessor(Param.class, new ParamAnnotationProcessor());
    this.registerParameterAnnotationProcessor(Body.class, new BodyAnnotationProcessor());
  }

  /**
   * Support {@link Request} and {@link Headers} at the class level.
   *
   * @return set of supported annotations at the class level.
   */
  @Override
  protected Collection<Class<? extends Annotation>> getSupportedClassAnnotations() {
    return Set.of(Request.class, Headers.class);
  }

  /**
   * Support the same items at the class level at the method level.
   *
   * @return a set of supported annotations at the method level.
   */
  @Override
  protected Collection<Class<? extends Annotation>> getSupportedMethodAnnotations() {
    return this.getSupportedClassAnnotations();
  }

  /**
   * Support the {@link Param} and {@link Body} annotations at the parameter level.
   *
   * @return the set of supported annotations at the parameter level.
   */
  @Override
  protected Collection<Class<? extends Annotation>> getSupportedParameterAnnotations() {
    return Set.of(Param.class, Body.class);
  }
}
