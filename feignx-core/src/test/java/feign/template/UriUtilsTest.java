package feign.template;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UriUtilsTest {

  @Test
  void skipEncoding_whenAlreadyEncoded() {
    String result = UriUtils.encode("%25");
    assertThat(result).isEqualTo("%25");
  }

}