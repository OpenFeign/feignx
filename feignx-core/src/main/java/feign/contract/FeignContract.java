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

package feign.contract;

import feign.TargetMethodDefinition;
import feign.http.HttpHeader;
import feign.http.HttpMethod;
import feign.support.StringUtils;
import feign.template.SimpleTemplateParameter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Contract that uses Feign annotations.
 */
public class FeignContract extends AbstractAnnotationDrivenContract {

  /**
   * Process any Annotations present on the Target Type.  Any values determined here should be
   * considered common for all methods in the Target.
   *
   * @param targetType to inspect.
   * @param targetMethodDefinition to store the resulting configuration.
   */
  @Override
  protected void processAnnotationsOnType(Class<?> targetType,
      TargetMethodDefinition targetMethodDefinition) {
    if (targetType.isAnnotationPresent(Request.class)) {
      this.processRequest(targetType.getAnnotation(Request.class), targetMethodDefinition);
    }
    if (targetType.isAnnotationPresent(Headers.class)) {
      this.processHeaders(targetType.getAnnotation(Headers.class), targetMethodDefinition);
    }
  }

  /**
   * Process any Annotations present on the Method.
   *
   * @param targetType containing the method.
   * @param method to inspect.
   * @param targetMethodDefinition to store the resulting configuration.
   */
  @Override
  protected void processAnnotationsOnMethod(Class<?> targetType, Method method,
      TargetMethodDefinition targetMethodDefinition) {
    if (method.isAnnotationPresent(Request.class)) {
      targetMethodDefinition
          .name(method.getName())
          .tag(this.getMethodTag(targetType, method))
          .returnType(this.getMethodReturnType(method));
      this.processRequest(method.getAnnotation(Request.class), targetMethodDefinition);
    }
    if (method.isAnnotationPresent(Headers.class)) {
      this.processHeaders(method.getAnnotation(Headers.class), targetMethodDefinition);
    }
  }

  /**
   * Process any Annotations present on the method Parameter.
   *
   * @param parameter to inspect.
   * @param parameterIndex of the parameter in the method signature.
   * @param targetMethodDefinition to store the resulting configuration.
   */
  @Override
  protected void processAnnotationsOnParameter(Parameter parameter, Integer parameterIndex,
      TargetMethodDefinition targetMethodDefinition) {
    if (parameter.isAnnotationPresent(Param.class)) {
      this.processParameter(
          parameter.getAnnotation(Param.class), parameterIndex, targetMethodDefinition);
    }
    if (parameter.isAnnotationPresent(Body.class)) {
      targetMethodDefinition.body(parameterIndex);
    }
  }

  /**
   * Process the HttpRequest annotation.
   *
   * @param request annotation to process.
   * @param targetMethodDefinition for the request.
   */
  private void processRequest(Request request, TargetMethodDefinition targetMethodDefinition) {
    String uri = (StringUtils.isNotEmpty(request.uri())) ? request.uri() : request.value();
    HttpMethod httpMethod = request.method();
    boolean followRedirects = request.followRedirects();

    targetMethodDefinition.uri(uri)
        .method(httpMethod)
        .followRedirects(followRedirects)
        .connectTimeout(request.connectTimeout())
        .readTimeout(request.readTimeout());
  }

  /**
   * Process the Headers annotation.
   *
   * @param headers annotation to process.
   * @param targetMethodDefinition for the request.
   */
  private void processHeaders(Headers headers, TargetMethodDefinition targetMethodDefinition) {
    if (headers.value().length != 0) {
      Header[] header = headers.value();
      for (Header value : header) {
        this.processHeader(value, targetMethodDefinition);
      }
    }
  }

  /**
   * Process the Header annotation.
   *
   * @param header annotation to process.
   * @param targetMethodDefinition for the header.
   */
  private void processHeader(Header header, TargetMethodDefinition targetMethodDefinition) {
    HttpHeader httpHeader = new HttpHeader(header.name());
    httpHeader.value(header.value());
    targetMethodDefinition.header(httpHeader);
  }

  /**
   * Process the Param annotation.
   *
   * @param parameter annotation to process.
   * @param index of the parameter in the method signature.
   * @param targetMethodDefinition for the parameter.
   */
  private void processParameter(Param parameter, Integer index,
      TargetMethodDefinition targetMethodDefinition) {
    String name = parameter.value();
    targetMethodDefinition.templateParameter(index, new SimpleTemplateParameter(name));
  }

  /**
   * Constructs a name for a Method that is formatted as a {@link com.sun.javadoc.SeeTag}.
   *
   * @param targetType containing the method.
   * @param method to inspect.
   * @return a See Tag inspired name for the method.
   */
  private String getMethodTag(Class<?> targetType, Method method) {
    StringBuilder sb = new StringBuilder()
        .append(targetType.getSimpleName())
        .append("#")
        .append(method.getName())
        .append("(");
    List<Type> parameters = Arrays.asList(method.getGenericParameterTypes());
    Iterator<Type> iterator = parameters.iterator();
    while (iterator.hasNext()) {
      Type parameter = iterator.next();
      sb.append(parameter.getTypeName());
      if (iterator.hasNext()) {
        sb.append(",");
      }
    }
    sb.append(")");
    return sb.toString();
  }

  private Type getMethodReturnType(Method method) {
    return method.getGenericReturnType();
  }
}
