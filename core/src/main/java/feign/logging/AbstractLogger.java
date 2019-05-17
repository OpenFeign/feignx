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

package feign.logging;

import feign.Header;
import feign.Logger;
import feign.Request;
import feign.Response;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

public abstract class AbstractLogger implements Logger {

  private final boolean enabled;
  private final boolean requestEnabled;
  private final boolean responseEnabled;
  private final boolean headersEnabled;

  protected AbstractLogger(boolean enabled, boolean requestEnabled, boolean responseEnabled,
      boolean headersEnabled) {
    this.enabled = enabled;
    this.requestEnabled = requestEnabled;
    this.responseEnabled = responseEnabled;
    this.headersEnabled = headersEnabled;
  }

  @Override
  public void logRequest(String methodName, Request request) {
    if (this.enabled) {
      StringJoiner joiner = new StringJoiner(", ", "[", "]");
      joiner.add("uri=" + request.uri());
      joiner.add("method=" + request.method());

      if (this.headersEnabled) {
        for (Header header : request.headers()) {
          StringJoiner headers = new StringJoiner(", ", "[", "]");
          this.logHeader(header, headers);
          joiner.add("headers=" + headers.toString());
        }
      }

      if (this.requestEnabled) {
        int length = request.contentLength();
        joiner.add("size=" + length);
        if (length != 0) {
          if (length < 512) {
            /* create a new String to hold the body */
            String body = new String(request.content(), StandardCharsets.UTF_8);
            joiner.add("body=" + body);
          } else {
            /* requests that are too large are not logged */
            joiner.add("body=binary data");
          }
        }
      }
      this.log(methodName, "Request: " + joiner.toString());
    }
  }

  @Override
  public void logResponse(String methodName, Response response) {
    if (this.enabled) {
      StringJoiner joiner = new StringJoiner(", ", "[", "]");
      joiner.add("status=" + response.status());
      joiner.add("reason=" + response.reason());
      joiner.add("length=" + response.contentLength());

      if (this.headersEnabled) {
        for (Header header : response.headers()) {
          StringJoiner headers = new StringJoiner(", ", "[", "]");
          this.logHeader(header, headers);
          joiner.add("headers=" + headers.toString());
        }
      }

      if (this.responseEnabled) {
        int length = response.contentLength();
        if (length != 0) {
          if (length < 512) {
            try {
              /* this forces us to read the entire response before logging */
              String body = new String(response.toByteArray(), StandardCharsets.UTF_8);
              joiner.add("body=" + body);
            } catch (IOException io) {
              this.log(methodName,
                  "IOException occurred when reading Response. " + io.getMessage());
            }
          } else {
            joiner.add("body=binary data");
          }
        }
      }

      this.log(methodName, "Response: " + joiner.toString());
    }

  }

  protected void logHeader(Header header, StringJoiner joiner) {
    joiner.add(header.name() + "=" + header.values());
  }

  protected abstract void log(String methodName, String message);
}
