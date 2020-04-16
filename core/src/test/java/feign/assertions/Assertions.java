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

package feign.assertions;

/**
 * Entry point for assertions of different data types. Each method in this class is a static factory for the
 * type-specific assertion objects.
 */
@javax.annotation.Generated(value="assertj-assertions-generator")
public class Assertions {

  /**
   * Creates a new instance of <code>{@link feign.http.HttpHeaderAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  @org.assertj.core.util.CheckReturnValue
  public static feign.assertions.HttpHeaderAssert assertThat(feign.http.HttpHeader actual) {
    return new feign.assertions.HttpHeaderAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link feign.http.HttpRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  @org.assertj.core.util.CheckReturnValue
  public static feign.assertions.HttpRequestAssert assertThat(feign.http.HttpRequest actual) {
    return new feign.assertions.HttpRequestAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link feign.http.HttpResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  @org.assertj.core.util.CheckReturnValue
  public static feign.assertions.HttpResponseAssert assertThat(feign.http.HttpResponse actual) {
    return new feign.assertions.HttpResponseAssert(actual);
  }

  /**
   * Creates a new <code>{@link Assertions}</code>.
   */
  protected Assertions() {
    // empty
  }
}
