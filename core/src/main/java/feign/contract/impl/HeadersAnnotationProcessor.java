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
import feign.contract.Headers;
import feign.contract.TargetMethodDefinition.Builder;

/**
 * Annotation Processor for the {@link Headers} annotation.
 */
public class HeadersAnnotationProcessor implements AnnotationProcessor<Headers> {

  private final HeaderAnnotationProcessor headerAnnotationProcessor;

  /**
   * Creates a new {@link HeadersAnnotationProcessor}.
   */
  public HeadersAnnotationProcessor() {
    this.headerAnnotationProcessor = new HeaderAnnotationProcessor();
  }

  @Override
  public void process(Headers annotation, Builder builder) {
    Header[] headers = annotation.value();
    if (headers.length != 0) {
      for (Header value : headers) {
        this.headerAnnotationProcessor.process(value, builder);
      }
    }
  }
}
