package feign;

import feign.http.HttpHeader;
import feign.http.HttpMethod;
import feign.http.RequestSpecification;
import feign.support.Assert;
import feign.template.TemplateParameter;
import feign.template.UriTemplate;
import feign.impl.type.TypeDefinitionFactory;
import feign.impl.type.TypeDefinition;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Configuration information for methods on a Target, considered immutable.
 */
public final class TargetMethodDefinition {

  private Target<?> target;
  private String name;
  private String tag;
  private transient TypeDefinition returnType;
  private UriTemplate template;
  private HttpMethod method = HttpMethod.GET;
  private Collection<HttpHeader> headers = new LinkedHashSet<>();
  private Map<Integer, TemplateParameter> parameterMap = new LinkedHashMap<>();
  private Integer bodyArgumentIndex = -1;
  private boolean followRedirects;
  private TypeDefinitionFactory typeDefinitionFactory = TypeDefinitionFactory.getInstance();

  /**
   * Creates a new {@link TargetMethodDefinition}
   */
  public TargetMethodDefinition(Target<?> target) {
    Assert.isNotNull(target, "target is required.");
    this.target = target;
  }

  /**
   * Creates a new {@link TargetMethodDefinition} from an existing instance.
   *
   * @param targetMethodDefinition to copy.
   */
  public TargetMethodDefinition(TargetMethodDefinition targetMethodDefinition) {
    /* copy the immutable values */
    this.target = targetMethodDefinition.target;
    this.name = targetMethodDefinition.name;
    this.tag = targetMethodDefinition.tag;
    this.returnType = targetMethodDefinition.returnType;
    this.template = targetMethodDefinition.template;
    this.method = targetMethodDefinition.method;
    this.parameterMap = targetMethodDefinition.parameterMap;
    this.bodyArgumentIndex = targetMethodDefinition.bodyArgumentIndex;
    this.followRedirects = targetMethodDefinition.followRedirects;

    /* create a deep copy of the headers */
    this.headers = targetMethodDefinition.headers.stream().map(
        header -> new HttpHeader(header.name(), header.values()))
        .collect(Collectors.toSet());

    /* create a copy of the uri template */
    if (targetMethodDefinition.template != null) {
      this.template = UriTemplate.create(targetMethodDefinition.template.toString());
    }
  }

  /**
   * Name of the Method on the Target this configuration is based.
   *
   * @return method name.
   */
  public String getName() {
    return name;
  }

  /**
   * Generic Return Type specified on the Method.
   *
   * @return return Type.
   */
  public Class<?> getReturnType() {
    return returnType.getType();
  }

  /**
   * Template Parameter registered at the specified method argument index.
   *
   * @param argumentIndex of the parameter.
   * @return the Template Parameter registered, if one exists.
   */
  public Optional<TemplateParameter> getTemplateParameter(Integer argumentIndex) {
    return Optional.ofNullable(parameterMap.get(argumentIndex));
  }

  /**
   * Unique Tag for this Method Configuration.
   *
   * @return a {@link com.sun.javadoc.SeeTag} version of the method signature.
   */
  public String getTag() {
    return this.tag;
  }

  /**
   * Argument Index registered for the value that should be used as the Request body.
   *
   * @return the request body argument index.
   */
  public Integer getBody() {
    return this.bodyArgumentIndex;
  }

  /**
   * Name of the Method.
   *
   * @param name method name.
   * @return the reference chain.
   */
  public TargetMethodDefinition name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Tag for the Method.
   *
   * @param tag method tag.
   * @return the reference chain.
   */
  public TargetMethodDefinition tag(String tag) {
    this.tag = tag;
    return this;
  }

  /**
   * Method Return Type.
   *
   * @param returnType of the method.
   * @return the reference chain.
   */
  public TargetMethodDefinition returnType(Type returnType) {
    this.returnType = this.typeDefinitionFactory.create(returnType, this.target.type());
    return this;
  }

  /**
   * Request URI for the Method.
   *
   * @param uri from the method.
   * @return the reference chain.
   */
  public TargetMethodDefinition uri(String uri) {
    Assert.isNotNull(uri, "uri is required");
    this.template = UriTemplate.create(uri);
    return this;
  }

  /**
   * Registers a {@link TemplateParameter} at the method argument index.
   *
   * @param argumentIndex in the method signature.
   * @param templateParameter to register.
   * @return the reference chain.
   */
  public TargetMethodDefinition templateParameter(
      Integer argumentIndex, TemplateParameter templateParameter) {
    this.parameterMap.put(argumentIndex, templateParameter);
    return this;
  }

  /**
   * Registers a {@link HttpHeader} for the method.
   *
   * @param header to register.
   * @return the reference chain.
   */
  public TargetMethodDefinition header(HttpHeader header) {
    Assert.isNotNull(header, "header is required.");
    this.headers.add(header);
    return this;
  }

  /**
   * Http Method for the Request defined.
   *
   * @param httpMethod for the request.
   * @return the reference chain.
   */
  public TargetMethodDefinition method(HttpMethod httpMethod) {
    this.method = httpMethod;
    return this;
  }

  /**
   * Registers which argument in the method signature containing the Request body.
   *
   * @param argumentIndex for the Request body object.
   * @return the reference chain.
   */
  public TargetMethodDefinition body(Integer argumentIndex) {
    this.bodyArgumentIndex = argumentIndex;
    return this;
  }

  /**
   * Determines if this request should automatically follow 3xx responses.
   *
   * @param followRedirects flag.
   * @return the reference chain.
   */
  public TargetMethodDefinition followRedirects(boolean followRedirects) {
    this.followRedirects = followRedirects;
    return this;
  }

  /**
   * Creates a {@link RequestSpecification} using the method configuration and the variables
   * provided.
   *
   * @param variables to use when expanding the method's {@link UriTemplate}.
   * @return a {@link RequestSpecification} instance with the expanded URI, headers, and body.
   */
  public RequestSpecification requestSpecification(Map<String, ?> variables) {
    RequestSpecification requestSpecification = new RequestSpecification();
    requestSpecification.uri(this.template.expand(variables))
        .method(this.method);
    if (!this.headers.isEmpty()) {
      for (HttpHeader header : headers) {
        header.values().forEach(value -> requestSpecification.header(header.name(), value));
      }
    }

    /* target the request */
    this.target.apply(requestSpecification);
    return requestSpecification;
  }

  /**
   * Determines if this Method Metadata contains no information.
   *
   * @return {@literal true} when no metadata present, {@literal false} otherwise.
   */
  public boolean isEmpty() {
    return this.template == null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof TargetMethodDefinition)) {
      return false;
    }
    TargetMethodDefinition that = (TargetMethodDefinition) obj;
    return name.equals(that.name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
