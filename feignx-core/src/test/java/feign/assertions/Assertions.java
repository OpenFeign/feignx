package feign.assertions;

/**
 * Entry point for assertions of different data types. Each method in this class is a static factory for the
 * type-specific assertion objects.
 */
@javax.annotation.Generated(value="assertj-assertions-generator")
public class Assertions {

  /**
   * Creates a new instance of <code>{@link feign.http.HttpHeaderAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  @org.assertj.core.util.CheckReturnValue
  public static feign.assertions.HttpHeaderAssert assertThat(feign.http.HttpHeader actual) {
    return new feign.assertions.HttpHeaderAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link feign.http.HttpRequestAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  @org.assertj.core.util.CheckReturnValue
  public static feign.assertions.HttpRequestAssert assertThat(feign.http.HttpRequest actual) {
    return new feign.assertions.HttpRequestAssert(actual);
  }

  /**
   * Creates a new instance of <code>{@link feign.http.HttpResponseAssert}</code>.
   *
   * @param actual the actual value.
   * @return the created assertion object.
   */
  @org.assertj.core.util.CheckReturnValue
  public static feign.assertions.HttpResponseAssert assertThat(feign.http.HttpResponse actual) {
    return new feign.assertions.HttpResponseAssert(actual);
  }

  /**
   * Creates a new <code>{@link Assertions}</code>.
   */
  protected Assertions() {
    // empty
  }
}
