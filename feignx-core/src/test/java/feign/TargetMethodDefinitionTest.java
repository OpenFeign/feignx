package feign;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

class TargetMethodDefinitionTest {

  @Test
  void getUri_alwaysReturnsAValue() {
    TargetMethodDefinition targetMethodDefinition = new TargetMethodDefinition(mock(Target.class));
    assertThat(targetMethodDefinition.getUri()).isNotNull();
  }

  @Test
  void equals_name_caseSensitive() {
    TargetMethodDefinition targetMethodDefinition = new TargetMethodDefinition(mock(Target.class));
    targetMethodDefinition.name("name");

    TargetMethodDefinition anotherDefinition = new TargetMethodDefinition(mock(Target.class));
    anotherDefinition.name("name");
    assertThat(targetMethodDefinition).isEqualTo(anotherDefinition);
  }

  @Test
  void notEqual_name_caseSensitive() {
    TargetMethodDefinition targetMethodDefinition = new TargetMethodDefinition(mock(Target.class));
    targetMethodDefinition.name("Name");

    TargetMethodDefinition anotherDefinition = new TargetMethodDefinition(mock(Target.class));
    anotherDefinition.name("name");
    assertThat(targetMethodDefinition).isNotEqualTo(anotherDefinition);
  }
}