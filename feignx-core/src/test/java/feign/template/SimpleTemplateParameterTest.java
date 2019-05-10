package feign.template;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SimpleTemplateParameterTest {

  @Test
  void encode_by_default() {
    SimpleTemplateParameter templateParameter = new SimpleTemplateParameter("name");
    assertThat(templateParameter.encode()).isTrue();
  }

  @Test
  void skip_encode_if_set() {
    SimpleTemplateParameter templateParameter = new SimpleTemplateParameter("name", false);
    assertThat(templateParameter.encode()).isFalse();
  }
}