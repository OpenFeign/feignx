package feign.template;

import feign.support.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * URI Template Expression factory.
 */
public class Expressions {

  public static final String MULTIPLE_VALUE_DELIMITER = ",";

  private static final Pattern EXPRESSION_PATTERN =
      Pattern.compile("^\\{(([+#./;?&]?)([\\d\\w_*,\\-\\[\\]%$]+)(:([\\d]*))?(,(.*))?)}$");

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
      Expression expression;

      /* extract the full variable specification */
      String spec = matcher.group(1);

      /* look to see if there are any modifiers in the first group */
      String modifier = matcher.group(2);

      /* split composite expressions */
      if (spec.contains(MULTIPLE_VALUE_DELIMITER)) {
        /* it's a composite expression, so we need to take everything after the modifier,
         * and split it up, so each expression can be treated like it's own.
         */
        CompositeExpression composite = new CompositeExpression(spec);
        String[] specs = spec.split(MULTIPLE_VALUE_DELIMITER);
        for (String specification : specs) {
          Expression contained = create(modifier, specification);
          composite.append(contained);
          composite.setDelimiter(contained.getDelimiter());
          composite.setPrefix(contained.getPrefix());
        }
        expression = composite;
      } else {
        /* get the expression for the singular specification */
        expression = create(modifier, spec);
      }
      return expression;
    } else {
      throw new IllegalArgumentException("Supplied variable specification is not valid.  Please "
          + "see RFC 6570 for more information how to construct a variable specification");
    }
  }

  private static Expression create(String modifier, String spec) {
    Expression expression;

    /* remove modifier */
    if (StringUtils.isNotEmpty(modifier) && spec.startsWith(modifier)) {
      spec = spec.substring(1);
    }

    /* expansion limit */
    int limit = -1;
    if (spec.contains(":")) {
      String expansionLimit = spec.substring(spec.indexOf(":") + 1);
      if (StringUtils.isNotEmpty(expansionLimit)) {
        try {
          /* read in the limit */
          limit = Integer.parseInt(expansionLimit);
        } catch (NumberFormatException nfe) {
          throw new IllegalArgumentException("Error occurred parsing the expansion limit for the "
              + "variable: " + spec + ".  Limit provided is not a valid integer.");
        }
      }
      spec = spec.substring(0, spec.indexOf(":"));
    }

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
        throw new IllegalStateException("Expression " + spec + " is not supported");
      }
    } else {
      expression = new SimpleExpression(spec);
    }
    expression.setLimit(limit);
    return expression;

  }

  /**
   * Determines if the provided variable specification is a valid Expression.
   *
   * @param variableSpec to evaluate.
   * @return {@literal true} if the specification is a valid Expression, {@literal false} otherwise.
   */
  public static boolean isExpression(String variableSpec) {
    return EXPRESSION_PATTERN.matcher(variableSpec).matches();
  }
}
