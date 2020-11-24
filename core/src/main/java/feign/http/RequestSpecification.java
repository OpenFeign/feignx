/*
 * Copyright 2019-2020 OpenFeign Contributors
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

package feign.http;

import feign.Header;
import feign.Request;
import feign.RequestEntity;
import feign.RequestOptions;
import feign.support.Assert;
import feign.support.StringUtils;
import feign.template.UriUtils;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Immutable model that represents the parts of an Http HttpRequest.
 */
public class RequestSpecification {

  private HttpMethod method;
  private URI uri;
  private final Map<String, Header> headers = new LinkedHashMap<>();
  private final Map<String, List<String>> parameters = new LinkedHashMap<>();
  private RequestEntity content;
  private long connectTimeout;
  private TimeUnit connectTimeoutUnit;
  private long readTimeout;
  private TimeUnit readTimeoutUnit;
  private boolean followRedirects;

  /**
   * Creates a new HttpRequest Specification.
   */
  public RequestSpecification() {
    this.method = HttpMethod.GET;
    this.content = null;
    this.followRedirects = true;
  }

  /**
   * Set the URI for the HttpRequest.
   *
   * @param uri of the HttpRequest, cannot be {@literal null}
   * @return the reference chain.
   */
  public RequestSpecification uri(URI uri) {
    Assert.isNotNull(uri, "uri is required.");
    this.uri = uri;
    return this;
  }

  /**
   * URI for the HttpRequest.
   *
   * @return HttpRequest URI, if set.
   */
  public Optional<URI> uri() {
    return Optional.ofNullable(this.uri);
  }

  /**
   * Http Method for the HttpRequest.
   *
   * @param method of the HttpRequest.
   * @return the reference chain.
   */
  public RequestSpecification method(HttpMethod method) {
    this.method = method;
    return this;
  }

  /**
   * Http HttpRequest Method.
   *
   * @return HttpRequest Method.
   */
  public HttpMethod method() {
    return this.method;
  }

  /**
   * Specify a Http Header on the HttpRequest.  If the Header is already present on the request and
   * the Header being requested supports multiple values, the value will be added.
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
   * Http Header on the HttpRequest.
   *
   * @param name of the Header.
   * @return the HttpHeader for the name, if set.
   */
  public Optional<Header> header(String name) {
    return Optional.ofNullable(this.headers.get(name));
  }

  /**
   * Specify a HttpRequest Parameter (Query Parameter).  If values for the parameter are already
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
   * Specify the HttpRequest content.
   *
   * @param content to include in the HttpRequest body.
   * @return the reference chain.
   */
  public RequestSpecification content(RequestEntity content) {
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
   * Creates a new {@link feign.Request} from this specification.
   *
   * @return a new HttpRequest instance.
   */
  public Request build() {
    /* rebuild the uri with the new query string */
    if (!this.parameters.isEmpty()) {
      String query = this.applyParameters(this.uri.getQuery());
      if (StringUtils.isNotEmpty(query)) {
        /* this implementation is a bit more complicated due to the fact that new URI()
         * can double-encode values when URI.create does not.
         */
        StringBuilder updated = new StringBuilder(this.uri.getScheme())
            .append("://");

        if (StringUtils.isNotEmpty(this.uri.getUserInfo())) {
          updated.append(this.uri.getUserInfo());
          updated.append("@");
        }
        updated.append(this.uri.getHost());

        if (this.uri.getPort() != -1) {
          updated.append(":");
          updated.append(this.uri.getPort());
        }

        if (StringUtils.isNotEmpty(this.uri.getPath())) {
          updated.append(this.uri.getPath());
        }

        updated.append("?");
        updated.append(query);

        if (StringUtils.isNotEmpty(this.uri.getFragment())) {
          updated.append("#");
          updated.append(this.uri.getFragment());
        }

        this.uri = URI.create(updated.toString());
      }
    }

    /* get the request options */
    RequestOptions options = this.getOptions();

    /* create a new HttpRequest */
    return new HttpRequest(
        this.uri,
        this.method,
        this.headers.values(),
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
          while (it.hasNext()) {
            String value = it.next();
            parameters.append(UriUtils.encode(entry.getKey()))
                .append("=")
                .append(UriUtils.encode(value));
            if (it.hasNext()) {
              parameters.append("&");
            }
          }
          parameters.append("&");
        });
    if (StringUtils.isNotEmpty(query)) {
      query += "&" + parameters.toString();
    } else {
      query = parameters.toString();
    }

    /* strip any remaining ampersands */
    if (query.endsWith("&")) {
      query = query.substring(0, query.length() - 1);
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
    return builder.setConnectTimeout(this.connectTimeout, this.connectTimeoutUnit)
        .setReadTimeout(this.readTimeout, this.readTimeoutUnit)
        .setFollowRedirects(this.followRedirects)
        .build();
  }
}
