package feign.template;

import feign.support.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Expressions {

  private static final Pattern EXPRESSION_PATTERN =
      Pattern.compile("^\\{([+#./;?&]?)([\\d\\w_*,\\-\\[\\]]+)(:([\\d]*))?}$");

  private static final String RESERVED_MODIFIER = "+";

  public static Expression create(String variableSpec) {
    /* parse the specification */
    Matcher matcher = EXPRESSION_PATTERN.matcher(variableSpec);
    if (matcher.matches()) {

      /* look to see if there are any modifiers in the first group */
      String modifiers = matcher.group(1);

      /* get the variable name */
      String variableName = matcher.group(2);

      Expression expression = null;
      if (StringUtils.isNotEmpty(modifiers)) {
        if (RESERVED_MODIFIER.equalsIgnoreCase(modifiers)) {
          expression = new ReservedExpression(variableName);
        }
      } else {
        expression = new SimpleExpression(variableName);
      }

      if (expression == null) {
        throw new IllegalStateException("Expression " + variableSpec + " is not supported");
      }

      /* expansion limit */
      int limit = -1;
      String expansionLimit = matcher.group(4);
      if (StringUtils.isNotEmpty(expansionLimit)) {
        try {
          /* read in the limit */
          limit = Integer.parseInt(expansionLimit);
        } catch (NumberFormatException nfe) {
          throw new IllegalArgumentException("Error occurred parsing the expansion limit for the "
              + "variable: " + variableName + ".  Limit provided is not a valid integer.");
        }
      }
      expression.setLimit(limit);
      return expression;
    } else {
      throw new IllegalArgumentException("Supplied variable specification is not valid.  Please "
          + "see RFC 6570 for more information how to construct a variable specification");
    }
  }

  public static boolean isExpression(String variableSpec) {
    return EXPRESSION_PATTERN.matcher(variableSpec).matches();
  }
}
