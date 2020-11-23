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

package feign.encoder;

import feign.RequestEntity;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

/**
 * Simple Request Entity backed by a String.
 */
public class StringRequestEntity implements RequestEntity {

  public static final String TEXT_PLAIN = "text/plain";
  private final byte[] data;
  private final int length;
  private final Charset charset = StandardCharsets.UTF_8;

  public StringRequestEntity(String content) {
    this.data = content.getBytes(StandardCharsets.UTF_8);
    this.length = data.length;
  }

  @Override
  public Optional<Charset> getCharset() {
    return Optional.of(this.charset);
  }

  @Override
  public int getContentLength() {
    return this.length;
  }

  @Override
  public String getContentType() {
    return TEXT_PLAIN;
  }

  @Override
  public byte[] getData() {
    return Arrays.copyOf(this.data, this.data.length);
  }
}
