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
import feign.contract.Request;
import feign.contract.TargetMethodDefinition.Builder;
import feign.http.HttpMethod;
import feign.support.StringUtils;

/**
 * Annotation processor for the {@link Request} annotation.  Responsible for parsing the top level
 * request information.
 */
public class RequestAnnotationProcessor implements AnnotationProcessor<Request> {

  /**
   * Read the uri, request method, and other request specific configuration.
   *
   * @param annotation to evaluate.
   * @param builder    with the current method context.
   */
  @Override
  public void process(Request annotation, Builder builder) {
    String uri = (StringUtils.isNotEmpty(annotation.uri())) ? annotation.uri() : annotation.value();
    HttpMethod httpMethod = annotation.method();
    boolean followRedirects = annotation.followRedirects();

    builder.uri(uri)
        .method(httpMethod)
        .followRedirects(followRedirects)
        .connectTimeout(annotation.connectTimeout())
        .readTimeout(annotation.readTimeout());
  }
}
