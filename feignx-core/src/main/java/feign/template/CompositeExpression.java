package feign.template;

import feign.support.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Expression that contains multiple expressions.
 */
public class CompositeExpression extends Expression {

  private final List<Expression> expressions = new ArrayList<>();
  private String prefix;
  private String delimiter;

  CompositeExpression(String variableSpecification) {
    super(variableSpecification);
  }

  void append(Expression expression) {
    this.expressions.add(expression);
  }

  void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }

  @Override
  protected boolean isCharacterAllowed(char character) {
    /* if it is allowed by any of the contained expressions, it's allowed */
    return this.expressions.stream()
        .anyMatch(expression -> expression.isCharacterAllowed(character));
  }

  @Override
  protected String getDelimiter() {
    return this.delimiter;
  }

  @Override
  protected String getPrefix() {
    return this.prefix;
  }

  @Override
  String expand(Map<String, ?> variables) {
    StringBuilder expansion = new StringBuilder();
    for (Expression value : this.expressions) {
      String expanded = value.expand(variables);

      if (expanded != null) {
        /* append the delimiter */
        this.appendDelimiter(expansion, this.getDelimiter());

        /* remove the prefix, if one is present, we will add it to the composite */
        if (StringUtils.isNotEmpty(this.getPrefix()) && expanded.startsWith(this.getPrefix())) {
          expanded = expanded.substring(1);
        }
        expansion.append(expanded);
      }
    }

    return this.getPrefix() + expansion.toString();
  }
}
