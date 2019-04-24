package feign.target;

import feign.support.Assert;
import java.net.URI;

public class UrlTarget<T> extends AbstractTarget<T> {

  private final String url;

  public UrlTarget(Class<T> type, String url) {
    super(type);
    Assert.isNotEmpty(url, "url is required.");
    Assert.isTrue(url, value -> URI.create(value).isAbsolute(), "url must be absolute.");
    this.url = url;
  }

  public UrlTarget(Class<T> type, String name, String url) {
    super(type, name);
    Assert.isNotEmpty(url, "url is required.");
    Assert.isTrue(url, value -> URI.create(value).isAbsolute(), "url must be absolute.");
    this.url = url;
  }

  public String getUrl() {
    return url;
  }
}
