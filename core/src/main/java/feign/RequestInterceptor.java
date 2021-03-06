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

import feign.http.RequestSpecification;
import java.util.function.Function;

/**
 * Function used to act on a {@link RequestSpecification} during request processing.  It is possible
 * to return an entirely new {@link RequestSpecification} from this component.
 */
public interface RequestInterceptor extends Function<RequestSpecification, RequestSpecification> {

  static RequestInterceptor identity() {
    return (t) -> t;
  }
}
