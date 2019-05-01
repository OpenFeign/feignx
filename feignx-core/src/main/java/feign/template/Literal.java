package feign.template;

import feign.support.Assert;

/**
 * Chunk for static values.
 */
public class Literal implements Chunk {

  private final String value;

  /**
   * Creates a new Literal Chunk.
   *
   * @param value of the chunk.
   */
  public Literal(String value) {
    Assert.isNotEmpty(value, "value is required.");
    this.value = value;
  }

  /**
   * Chunk Value.
   *
   * @return the literal value.
   */
  @Override
  public String getValue() {
    return this.value;
  }
}
