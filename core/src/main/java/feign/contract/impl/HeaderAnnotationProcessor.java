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

import feign.contract.AnnotationProcessor;
import feign.contract.Header;
import feign.contract.TargetMethodDefinition.Builder;
import feign.http.HttpHeader;

/**
 * Annotation processor for the {@link Header} annotation.  Evaluates and prepares an HTTP
 * Header.
 */
public class HeaderAnnotationProcessor implements AnnotationProcessor<Header> {

  @Override
  public void process(Header annotation, Builder builder) {
    HttpHeader httpHeader = new HttpHeader(annotation.name());
    httpHeader.value(annotation.value());
    builder.header(httpHeader);
  }
}
