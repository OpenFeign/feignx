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

package feign.template;

import feign.support.StringUtils;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * URI Template Expression factory.
 */
public class Expressions {

  private static final String MULTIPLE_VALUE_DELIMITER = ",";
  private static final Pattern EXPRESSION_PATTERN = Pattern.compile("^\\{([+#./;?&]?)(.*)}$");
  private static final String RESERVED_MODIFIER = "+";
  private static final String FRAGMENT_MODIFIER = "#";
  private static final String DOT_MODIFIER = ".";
  private static final String PATH_MODIFIER = "/";
  private static final String PATH_STYLE_MODIFIER = ";";
  private static final String FORM_STYLE_MODIFIER = "?";
  private static final String FORM_CONT_STYLE_MODIFIER = "&";

  /**
   * Creates a new Expression.
   *
   * @param expressionSpec with the expression specification.
   * @return an Expression instance based on the specification.
   */
  public static Expression create(String expressionSpec) {

    /* parse the specification */
    Matcher matcher = EXPRESSION_PATTERN.matcher(expressionSpec);
    if (matcher.matches()) {
      /* look to see if there are any modifiers in the first group */
      String operator = matcher.group(1);

      /* extract the full variable specification */
      String spec = matcher.group(2);

      /* split the remainder of the expression */
      if (StringUtils.isNotEmpty(spec)) {
        /* create a new expression */
        return create(operator, Arrays.asList(spec.split(MULTIPLE_VALUE_DELIMITER)));
      } else {
        throw new IllegalArgumentException("Supplied expression: " + expressionSpec
            + " is not valid.  Please see RFC 6570 for more information how to construct an "
            + "expression");
      }
    } else {
      throw new IllegalArgumentException("Supplied expression: " + expressionSpec
          + " is not valid.  Please see RFC 6570 for more information how to construct an "
          + "expression");
    }
  }

  /**
   * Create a new Expression.
   *
   * @param operator for this expresion, can be {@literal null}.
   * @param variables found in the expression.
   * @return an {@link Expression} instance.
   */
  private static Expression create(String operator, List<String> variables) {
    Expression expression;

    ExpansionPolicy policy;
    if (StringUtils.isNotEmpty(operator)) {
      if (RESERVED_MODIFIER.equalsIgnoreCase(operator)) {
        policy = ReservedExpansionPolicy.getInstance();
      } else if (FRAGMENT_MODIFIER.equalsIgnoreCase(operator)) {
        policy = FragmentExpansionPolicy.getInstance();
      } else if (DOT_MODIFIER.equalsIgnoreCase(operator)) {
        policy = DotExpansionPolicy.getInstance();
      } else if (PATH_MODIFIER.equalsIgnoreCase(operator)) {
        policy = PathSegmentExpansionPolicy.getInstance();
      } else if (PATH_STYLE_MODIFIER.equalsIgnoreCase(operator)) {
        policy = PathStyleExpansionPolicy.getInstance();
      } else if (FORM_STYLE_MODIFIER.equalsIgnoreCase(operator)) {
        policy = FormStyleExpansionPolicy.getInstance();
      } else if (FORM_CONT_STYLE_MODIFIER.equalsIgnoreCase(operator)) {
        policy = FormContinuationStyleExpansionPolicy.getInstance();
      } else {
        throw new IllegalStateException("Expression " + variables + " is not supported");
      }
    } else {
      policy = SimpleExpansionPolicy.getInstance();
    }

    return new Expression(variables, operator, policy);

  }

  /**
   * Determines if the provided variable specification is a valid Expression.
   *
   * @param variableSpec to evaluate.
   * @return {@literal true} if the specification is a valid Expression, {@literal false} otherwise.
   */
  static boolean isExpression(String variableSpec) {
    return EXPRESSION_PATTERN.matcher(variableSpec).matches();
  }
}
