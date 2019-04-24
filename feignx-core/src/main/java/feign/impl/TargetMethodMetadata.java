package feign.impl;

import feign.http.RequestSpecification;
import feign.template.TemplateParameter;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class TargetMethodMetadata {

  private String name;
  private transient Type returnType;
  private URI uri;
  private Map<Integer, TemplateParameter> parameterMap = new LinkedHashMap<>();

  public TargetMethodMetadata() {
    super();
  }

  public TargetMethodMetadata(TargetMethodMetadata targetMethodMetadata) {
    this.name = targetMethodMetadata.name;
    this.returnType = targetMethodMetadata.returnType;
    this.uri = targetMethodMetadata.uri;
    this.parameterMap = targetMethodMetadata.parameterMap;
  }

  public String getName() {
    return name;
  }

  public Type getReturnType() {
    return returnType;
  }

  public URI getUri() {
    return uri;
  }

  public TemplateParameter getTemplateParameter(Integer argumentIndex) {
    return parameterMap.get(argumentIndex);
  }

  public TargetMethodMetadata name(String name) {
    this.name = name;
    return this;
  }

  public TargetMethodMetadata returnType(Type responseType) {
    this.returnType = responseType;
    return this;
  }

  public TargetMethodMetadata uri(URI uri) {
    this.uri = uri;
    return this;
  }

  public TargetMethodMetadata templateParameter(
      Integer argumentIndex, TemplateParameter templateParameter) {
    this.parameterMap.put(argumentIndex, templateParameter);
    return this;
  }

  public RequestSpecification requestSpecification() {
    return null;
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof TargetMethodMetadata)) {
      return false;
    }
    TargetMethodMetadata that = (TargetMethodMetadata) obj;
    return name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
