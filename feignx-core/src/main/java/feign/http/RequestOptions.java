package feign.http;

import java.util.concurrent.TimeUnit;

/**
 * Per HttpRequest Options.
 */
@SuppressWarnings("WeakerAccess")
public final class RequestOptions {

  /* default read timeout is 60 seconds */
  public static final long DEFAULT_READ_TIMEOUT = 60 * 1000;

  /* default connect timeout is 10 seconds */
  public static final long DEFAULT_CONNECT_TIMEOUT = 10 * 1000;

  private final boolean followRedirects;
  private final long readTimeout;
  private final long connectTimeout;

  /**
   * Creates a new RequestOptions.
   *
   * @param followRedirects flag to determine if 3xx responses should be automatically followed.
   * @param readTimeout for how long to wait when reading data from the target, in milliseconds.
   * @param connectTimeout for how long to wait when connecting to a target, in milliseconds.
   */
  private RequestOptions(boolean followRedirects, long readTimeout, long connectTimeout) {
    this.followRedirects = followRedirects;
    this.readTimeout = readTimeout;
    this.connectTimeout = connectTimeout;
  }

  public boolean isFollowRedirects() {
    return followRedirects;
  }

  public int getReadTimeout() {
    return Math.toIntExact(this.readTimeout);
  }

  public int getConnectTimeout() {
    return Math.toIntExact(this.connectTimeout);
  }

  /**
   * A RequestOptions Builder instance.
   *
   * @return a new builder instance.
   */
  public static Builder builder() {
    return new Builder();
  }

  static class Builder {
    private boolean followRedirects = true;
    private long readTimeout = DEFAULT_READ_TIMEOUT;
    private long connectTimeout = DEFAULT_CONNECT_TIMEOUT;


    public Builder setFollowRedirects(boolean followRedirects) {
      this.followRedirects = followRedirects;
      return this;
    }

    public Builder setReadTimeout(long readTimeout, TimeUnit timeUnit) {
      if (readTimeout != 0) {
        this.readTimeout = timeUnit.toMillis(readTimeout);
      }
      return this;
    }

    public Builder setConnectTimeout(long connectTimeout, TimeUnit timeUnit) {
      if (connectTimeout != 0) {
        this.connectTimeout = timeUnit.toMillis(connectTimeout);
      }
      return this;
    }

    public RequestOptions build() {
      return new RequestOptions(this.followRedirects, this.readTimeout, this.connectTimeout);
    }
  }
}
