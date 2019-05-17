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

package feign;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Per Request Options.
 */
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

  public static class Builder {
    private boolean followRedirects = true;
    private long readTimeout = DEFAULT_READ_TIMEOUT;
    private long connectTimeout = DEFAULT_CONNECT_TIMEOUT;

    /**
     * Set if this request should automatically follow 3xx response codes.
     *
     * @param followRedirects flag.
     * @return the builder chain.
     */
    public Builder setFollowRedirects(boolean followRedirects) {
      this.followRedirects = followRedirects;
      return this;
    }

    /**
     * Set the read timout.
     *
     * @param readTimeout value.
     * @param timeUnit for the timeout value.
     * @return the builder chain.
     */
    public Builder setReadTimeout(long readTimeout, TimeUnit timeUnit) {
      if (readTimeout != 0) {
        this.readTimeout = timeUnit.toMillis(readTimeout);
      }
      return this;
    }

    /**
     * Set the connect timeout.
     *
     * @param connectTimeout value.
     * @param timeUnit for the timeout value.
     * @return the builder chain.
     */
    public Builder setConnectTimeout(long connectTimeout, TimeUnit timeUnit) {
      if (connectTimeout != 0) {
        this.connectTimeout = timeUnit.toMillis(connectTimeout);
      }
      return this;
    }

    /**
     * Build the RequestOptions instance.
     *
     * @return a new RequestOptions instance.
     */
    public RequestOptions build() {
      return new RequestOptions(this.followRedirects, this.readTimeout, this.connectTimeout);
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof RequestOptions)) {
      return false;
    }
    RequestOptions options = (RequestOptions) obj;
    return followRedirects == options.followRedirects
        && readTimeout == options.readTimeout
        && connectTimeout == options.connectTimeout;
  }

  @Override
  public int hashCode() {
    return Objects.hash(followRedirects, readTimeout, connectTimeout);
  }

  @Override
  public String toString() {
    return "RequestOptions [" + "followRedirects=" + followRedirects
        + ", readTimeout=" + readTimeout
        + ", connectTimeout=" + connectTimeout
        + "]";
  }
}
