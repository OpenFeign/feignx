package feign.template;

import feign.support.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Expressions {

  private static final Pattern EXPRESSION_PATTERN =
      Pattern.compile("^\\{([+#./;?&]?)([\\d\\w_*,\\-\\[\\]]+)(:([\\d]*))?}$");

  public static Expression create(String variableSpec) {
    /* parse the specification */
    Matcher matcher = EXPRESSION_PATTERN.matcher(variableSpec);
    if (matcher.matches()) {
      /* look to see if there are any modifiers in the first group */
      String modifiers = matcher.group(1);
      if (StringUtils.isNotEmpty(modifiers)) {
        /* unsupported at this time */
        throw new IllegalArgumentException("Only Simple Expressions are supported at this time.");
      }

      /* get the variable name */
      String variableName = matcher.group(2);

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
      return new SimpleExpression(variableName, limit);
    } else {
      throw new IllegalArgumentException("Supplied variable specification is not valid.  Please "
          + "see RFC 6570 for more information how to construct a variable specification");
    }
  }

  public static boolean isExpression(String variableSpec) {
    return EXPRESSION_PATTERN.matcher(variableSpec).matches();
  }
}
