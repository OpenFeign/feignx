package feign.http;

import feign.support.Assert;
import feign.support.StringUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Immutable model that represents the parts of an Http Request.
 */
public final class RequestSpecification {

  private HttpMethod method;
  private URI uri;
  private final Map<String, HttpHeader> headers = new LinkedHashMap<>();
  private final Map<String, List<String>> parameters = new LinkedHashMap<>();
  private byte[] content;
  private long connectTimeout;
  private TimeUnit connectTimeoutUnit;
  private long readTimeout;
  private TimeUnit readTimeoutUnit;
  private boolean followRedirects;

  /**
   * Creates a new Request Specification.
   */
  public RequestSpecification() {
    this.method = HttpMethod.GET;
    this.content = null;
    this.followRedirects = true;
  }

  /**
   * The content slated to be sent.
   *
   * @return Request Content, if set.
   */
  public Optional<Object> content() {
    return Optional.ofNullable(this.content);
  }

  /**
   * URI for the Request.
   *
   * @return Request URI, if set.
   */
  public Optional<URI> uri() {
    return Optional.ofNullable(this.uri);
  }

  /**
   * Http Request Method.
   *
   * @return Request Method.
   */
  public HttpMethod method() {
    return this.method;
  }

  /**
   * Http Header on the Request.
   *
   * @param name of the Header.
   * @return the HttpHeader for the name, if set.
   */
  public Optional<HttpHeader> header(String name) {
    return Optional.ofNullable(this.headers.get(name));
  }

  /**
   * Set the URI for the Request.
   *
   * @param uri of the Request, cannot be {@literal null}
   * @return the reference chain.
   */
  public RequestSpecification uri(URI uri) {
    Assert.isNotNull(uri, "uri is required.");
    this.uri = uri;
    return this;
  }

  /**
   * Http Method for the Request.
   *
   * @param method of the Request.
   * @return the reference chain.
   */
  public RequestSpecification method(HttpMethod method) {
    this.method = method;
    return this;
  }

  /**
   * Specify a Http Header on the Request.  If the Header is already present
   * on the request and the Header being requested supports multiple values,
   * the value will be added.
   *
   * @param name of the Header.
   * @param value for the Header.
   * @return the reference chain.
   * @throws IllegalStateException if the Header being updated does not support multiple values.
   */
  public RequestSpecification header(String name, String value) {
    if (this.headers.containsKey(name)) {
      this.headers.get(name).value(value);
    } else {
      HttpHeader header = new HttpHeader(name);
      header.value(value);
      this.headers.put(name, header);
    }
    return this;
  }

  /**
   * Specify a Request Parameter (Query Parameter).  If values for the parameter are already
   * present, the value provided is added to the list.
   *
   * @param name of the Parameter.
   * @param value for the Parameter.
   * @return the reference chain.
   */
  public RequestSpecification parameter(String name, String value) {
    if (this.parameters.containsKey(name)) {
      this.parameters.get(name).add(value);
    } else {
      List<String> values = new ArrayList<>();
      values.add(value);
      this.parameters.put(name, values);
    }
    return this;
  }

  /**
   * Specify the Request content.
   *
   * @param content to include in the Request body.
   * @return the reference chain.
   */
  public RequestSpecification content(byte[] content) {
    this.content = content;
    return this;
  }

  /**
   * Specify the amount of time to wait before giving up, when reading the response.
   *
   * @param timeout to wait.
   * @param timeUnit the timeout value is in.
   * @return the reference chain.
   */
  public RequestSpecification readTimeout(long timeout, TimeUnit timeUnit) {
    Assert.isNotNull(timeUnit, "timeUnit is required.");
    this.readTimeout = timeout;
    this.readTimeoutUnit = timeUnit;
    return this;
  }

  /**
   * Specify the amount of time to wait before giving up, when connecting to the target host.
   *
   * @param timeout to wait.
   * @param timeUnit the timeout value is in.
   * @return the reference chain.
   */
  public RequestSpecification connectTimeout(long timeout, TimeUnit timeUnit) {
    Assert.isNotNull(timeUnit, "timeUnit is required.");
    this.connectTimeout = timeout;
    this.connectTimeoutUnit = timeUnit;
    return this;
  }

  /**
   * Specify if this request should automatically follow any 3xx response codes.
   *
   * @param followRedirects flag.
   * @return the reference chain.
   */
  public RequestSpecification followRedirects(boolean followRedirects) {
    this.followRedirects = followRedirects;
    return this;
  }

  /**
   * Creates a new {@link Request} from this specification.
   * @return a new Request instance.
   */
  public Request build() {
    /* rebuild the uri with the new query string */
    if (!this.parameters.isEmpty()) {
      try {
        String query = this.applyParameters(this.uri.getQuery());
        if (StringUtils.isNotEmpty(query)) {
          this.uri = new URI(this.uri.getScheme(),
              this.uri.getUserInfo(),
              this.uri.getHost(),
              this.uri.getPort(),
              this.uri.getPath(),
              query,
              this.uri.getFragment());
        }
      } catch (URISyntaxException se) {
        throw new IllegalArgumentException(se);
      }
    }

    /* get the request options */
    RequestOptions options = this.getOptions();

    /* create a new Request */
    return new Request(
        this.uri,
        this.method,
        this.headers.values().toArray(new HttpHeader[]{}),
        options,
        this.content);
  }

  /**
   * Apply any parameters added directly to the query string.
   *
   * @param query to add the parameters to.
   * @return the updated query.
   */
  private String applyParameters(String query) {
    /* append any parameters added here to the query part of the uri */
    StringBuilder parameters = new StringBuilder();
    this.parameters.entrySet().stream()
        .filter(entry -> !entry.getValue().isEmpty())
        .forEach(entry -> {
          Iterator<String> it = entry.getValue().iterator();
          while(it.hasNext()) {
            String value = it.next();
            parameters.append(entry.getKey())
                .append("=")
                .append(value);
            if (it.hasNext()) {
              parameters.append("&");
            }
          }
        });
    if (StringUtils.isNotEmpty(query)) {
      query += "&" + parameters.toString();
    } else {
      query = parameters.toString();
    }
    return query;
  }

  /**
   * Build a new RequestOptions instance from the values on this instance.
   *
   * @return a new RequestOptions instance.
   */
  private RequestOptions getOptions() {
    RequestOptions.Builder builder = RequestOptions.builder();
    if (this.connectTimeoutUnit != null) {
      builder.setConnectTimeout(this.connectTimeout, this.connectTimeoutUnit);
    }
    if (this.readTimeoutUnit != null) {
      builder.setReadTimeout(this.readTimeout, this.readTimeoutUnit);
    }
    return builder.setFollowRedirects(this.followRedirects)
        .build();
  }
}
