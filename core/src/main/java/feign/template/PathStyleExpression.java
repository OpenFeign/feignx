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

package feign.template;

import feign.support.StringUtils;

/**
 * Expression that use the semi-colon {@code ;} character as a prefix and exploded delimiter,
 * allowing for expansion of path parameters.
 */
public class PathStyleExpression extends SimpleExpression {

  private static final String SEMI = ";";

  PathStyleExpression(String variableSpecification) {
    super(variableSpecification, true);
  }

  @Override
  protected String getDelimiter() {
    return SEMI;
  }

  @Override
  protected String getPrefix() {
    return SEMI;
  }

  @Override
  protected void appendNamedResult(String name, Object result, StringBuilder builder) {
    /* append the name */
    builder.append(this.encode(name));

    /* check the result, first to see if it's 'empty' */
    if (result instanceof String) {
      if (StringUtils.isNotEmpty((String) result)) {
        /* append the equals */
        builder.append("=");
      }
    } else {
      /* always append otherwise */
      builder.append("=");
    }

    /* append the result */
    builder.append(result);
  }
}
