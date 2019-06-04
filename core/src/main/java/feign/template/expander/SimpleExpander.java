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

package feign.template.expander;

import feign.support.StringUtils;
import feign.template.Expression;
import feign.template.ExpressionExpander;
import feign.template.ExpressionVariable;
import feign.template.UriUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Expression Expander that relies on the values {@link Object#toString()}.  This expander will
 * honor any prefix limits.
 */
public class SimpleExpander implements ExpressionExpander {

  @Override
  public String expand(ExpressionVariable variable, Object value) {

    /* expand the value */
    String result = value.toString();

    /* apply any limits */
    if (variable.getPrefix() > 0) {
      /* prefix the result */
      int limit = (variable.getPrefix() > result.length()) ? result.length() : variable.getPrefix();
      result = result.substring(0, limit);
    }

    Expression expression = variable.getExpression();
    if (expression.expandNamedParameters()) {
      String variableName = this.encode(expression, variable.getName());
      String variableValue = this.encode(expression, result);
      StringBuilder namedResult = new StringBuilder(variableName);

      if (StringUtils.isEmpty(variableValue)) {
        if (expression.isFormStyle()) {
          namedResult.append("=");
        }
      } else {
        namedResult.append("=").append(variableValue);
      }
      result = namedResult.toString();
    } else {
      result = this.encode(variable.getExpression(), result);
    }

    /* return the pct-encoded result */
    return result;
  }

  String encode(Expression expression, String value) {
    if (!UriUtils.isPctEncoded(value)) {
      byte[] data = value.getBytes(StandardCharsets.UTF_8);

      try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
        for (byte b : data) {
          if (expression.isCharacterAllowed((char) b)) {
            bos.write(b);
          } else {
            UriUtils.pctEncode(b, bos);
          }
        }
        return new String(bos.toByteArray());
      } catch (IOException ioe) {
        throw new IllegalStateException("Error occurred during encoding of the uri: "
            + ioe.getMessage(), ioe);
      }
    }
    return value;
  }
}
