package feign.contract;

import feign.http.HttpHeader;
import feign.http.HttpMethod;
import feign.TargetMethodDefinition;
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
   * Process any Annotations present on the Target Type.  Any values determined here should
   * be considered common for all methods in the Target.
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
   * Process the Request annotation.
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
        .followRedirects(followRedirects);
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
  private void processParameter(Param parameter, Integer index, TargetMethodDefinition targetMethodDefinition) {
    String name = parameter.value();
    boolean encode = parameter.encode();
    targetMethodDefinition.templateParameter(index, new SimpleTemplateParameter(name, encode));
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
    while(iterator.hasNext()) {
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
    Type genericType = method.getGenericReturnType();
    return genericType;
  }
}
