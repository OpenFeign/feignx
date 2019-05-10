package feign.template;

import feign.support.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * URI Template Expression factory.
 */
public class Expressions {

  private static final Pattern EXPRESSION_PATTERN =
      Pattern.compile("^\\{([+#./;?&]?)([\\d\\w_*,\\-\\[\\]]+)(:([\\d]*))?}$");

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
   * @param variableSpec with the expression specification.
   * @return an Expression instance based on the specification.
   */
  public static Expression create(String variableSpec) {
    /* parse the specification */
    Matcher matcher = EXPRESSION_PATTERN.matcher(variableSpec);
    if (matcher.matches()) {

      /* look to see if there are any modifiers in the first group */
      String modifier = matcher.group(1);

      /* get the variable name */
      String spec = matcher.group(2);

      Expression expression;
      if (StringUtils.isNotEmpty(modifier)) {
        if (RESERVED_MODIFIER.equalsIgnoreCase(modifier)) {
          expression = new ReservedExpression(spec);
        } else if (FRAGMENT_MODIFIER.equalsIgnoreCase(modifier)) {
          expression = new FragmentExpression(spec);
        } else if (DOT_MODIFIER.equalsIgnoreCase(modifier)) {
          expression = new DotExpression(spec);
        } else if (PATH_MODIFIER.equalsIgnoreCase(modifier)) {
          expression = new PathSegmentExpression(spec);
        } else if (PATH_STYLE_MODIFIER.equalsIgnoreCase(modifier)) {
          expression = new PathStyleExpression(spec);
        } else if (FORM_STYLE_MODIFIER.equalsIgnoreCase(modifier)) {
          expression = new FormStyleExpression(spec);
        } else if (FORM_CONT_STYLE_MODIFIER.equalsIgnoreCase(modifier)) {
          expression = new FormContinuationStyleExpression(spec);
        } else {
          throw new IllegalStateException("Expression " + variableSpec + " is not supported");
        }
      } else {
        expression = new SimpleExpression(spec);
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
              + "variable: " + spec + ".  Limit provided is not a valid integer.");
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
