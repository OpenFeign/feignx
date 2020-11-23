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

package feign;

import java.nio.charset.Charset;
import java.util.Optional;

/**
 * Represents the content of a {@link Request}.
 */
public interface RequestEntity {

  /**
   * Character Set this entity is encoded in.
   *
   * @return the entity {@link Charset}.
   */
  Optional<Charset> getCharset();

  /**
   * Length of the entity data.
   *
   * @return entity data length.
   */
  int getContentLength();

  /**
   * Content Type of the entity.
   *
   * @return String representation of a MIME-Type or any other acceptable Content Type.
   */
  String getContentType();

  /**
   * Entity Data.
   *
   * @return entity data.
   */
  byte[] getData();
}
