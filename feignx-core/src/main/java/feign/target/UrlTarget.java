package feign.target;

import feign.support.Assert;
import java.net.URI;

public class UrlTarget<T> extends AbstractTarget<T> {

  private final String uri;

  public UrlTarget(Class<T> type, String uri) {
    super(type);
    Assert.isNotEmpty(uri, "uri is required.");
    Assert.isTrue(uri, value -> URI.create(value).isAbsolute(), "uri must be absolute.");
    this.uri = uri;
  }

  public UrlTarget(Class<T> type, String name, String uri) {
    super(type, name);
    Assert.isNotEmpty(uri, "uri is required.");
    Assert.isTrue(uri, value -> URI.create(value).isAbsolute(), "uri must be absolute.");
    this.uri = uri;
  }

  public String getUri() {
    return uri;
  }
}
