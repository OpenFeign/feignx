package feign.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import feign.Target;
import org.junit.jupiter.api.Test;

class UriTargetTest {

  @Test
  void canCreate_withTypeAsName() {
    Target<?> target = new UriTarget<>(String.class, "https://www.example.com");
    assertThat(target.name()).isEqualTo("String");
  }

  @Test
  void canCreate_withCustomName() {
    Target<?> target = new UriTarget<>(String.class, "Sample", "https://www.example.com");
    assertThat(target.name()).isEqualTo("Sample");
  }

  @Test
  void uri_mustBeAbsolute() {
    assertThrows(IllegalStateException.class,
        () -> new UriTarget<>(String.class, "/relative"));
  }
}