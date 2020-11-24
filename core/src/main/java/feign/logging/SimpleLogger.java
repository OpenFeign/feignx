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

package feign.logging;

import feign.support.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Simple Logger implementation backed by SLF4J.
 */
public class SimpleLogger extends AbstractLogger {

  private final Logger logger;

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Creates a new Simple Logger.
   *
   * @param enabled if logging should be enabled.
   * @param requestEnabled if request logging should be enabled.
   * @param responseEnabled if response logging should be enabled.
   * @param headersEnabled if header logging should be enabled.
   */
  private SimpleLogger(boolean enabled, boolean requestEnabled, boolean responseEnabled,
      boolean headersEnabled) {
    this(SimpleLogger.class.getName(), enabled, requestEnabled, responseEnabled, headersEnabled);
  }

  /**
   * Creates a new Simple Logger.
   *
   * @param name of the Logger.
   * @param enabled if logging should be enabled.
   * @param requestEnabled if request logging should be enabled.
   * @param responseEnabled if response logging should be enabled.
   * @param headersEnabled if header logging should be enabled.
   */
  private SimpleLogger(String name, boolean enabled, boolean requestEnabled,
      boolean responseEnabled,
      boolean headersEnabled) {
    super(enabled, requestEnabled, responseEnabled, headersEnabled);
    this.logger = LoggerFactory.getLogger(name);
  }

  /**
   * Log the Message.  Places the Method name on the MDC.
   *
   * @param methodName for context.
   * @param message to log.
   */
  @Override
  protected void log(String methodName, String message) {
    MDC.put("method", methodName);
    try {
      this.logger.info(String.format("%s() %s", methodName, message));
    } finally {
      MDC.clear();
    }
  }

  /**
   * Simple Logger Builder.
   */
  public static class Builder {

    private boolean enabled = false;
    private boolean requestEnabled = false;
    private boolean responseEnabled = false;
    private boolean headersEnabled = false;
    private String name;

    /**
     * Set the name of the Logger.
     *
     * @param name for the logger.
     * @return the builder chain.
     */
    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    /**
     * Set if Logging should be enabled.
     *
     * @param enabled flag.
     * @return the builder chain.
     */
    public Builder setEnabled(boolean enabled) {
      this.enabled = enabled;
      return this;
    }

    /**
     * Set if Request information should be logged.
     *
     * @param requestEnabled flag.
     * @return the builder chain.
     */
    public Builder setRequestEnabled(boolean requestEnabled) {
      this.enabled = true;
      this.requestEnabled = requestEnabled;
      return this;
    }

    /**
     * Set if Response information should be logged.
     *
     * @param responseEnabled flag.
     * @return the builder chain.
     */
    public Builder setResponseEnabled(boolean responseEnabled) {
      this.enabled = true;
      this.responseEnabled = responseEnabled;
      return this;
    }

    /**
     * Set if Header information should be logged.
     *
     * @param headersEnabled flag.
     * @return the builder chain.
     */
    public Builder setHeadersEnabled(boolean headersEnabled) {
      this.enabled = true;
      this.headersEnabled = headersEnabled;
      return this;
    }

    /**
     * Build a new Simple Logger instance.
     *
     * @return a new SimpleLogger instance.
     */
    public SimpleLogger build() {
      if (StringUtils.isNotEmpty(this.name)) {
        return new SimpleLogger(this.name, this.enabled, this.requestEnabled, this.responseEnabled,
            this.headersEnabled);
      }
      return new SimpleLogger(this.enabled, this.requestEnabled, this.responseEnabled,
          this.headersEnabled);
    }
  }
}
