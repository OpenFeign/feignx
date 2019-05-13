package feign.proxy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import feign.FeignConfiguration;
import feign.Target;
import feign.TargetMethodDefinition;
import feign.TargetMethodHandler;
import feign.TargetMethodHandlerFactory;
import feign.impl.UriTarget;
import java.lang.reflect.Method;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProxyTargetTest {

  @Mock
  private TargetMethodHandlerFactory targetMethodHandlerFactory;

  @Mock
  private FeignConfiguration feignConfiguration;

  @Mock
  private TargetMethodHandler targetMethodHandler;

  private ProxyTarget target;

  @SuppressWarnings("unchecked")
  @BeforeEach
  void setUp() {
    Target<?> uriTarget = new UriTarget<>(ProxyInterface.class, "https://www.example.com");
    TargetMethodDefinition targetMethodDefinition = new TargetMethodDefinition(uriTarget);
    targetMethodDefinition.name("test");

    when(this.feignConfiguration.getTarget()).thenReturn((Target<Object>) uriTarget);
    when(this.targetMethodHandlerFactory.create(any(TargetMethodDefinition.class),
        any(FeignConfiguration.class))).thenReturn(this.targetMethodHandler);

    this.target = new ProxyTarget(
        Collections.singletonList(targetMethodDefinition), this.targetMethodHandlerFactory,
        this.feignConfiguration);
  }

  @Test
  void toString_isNotProxied() throws Throwable {
    Method method = ProxyTarget.class.getMethod("toString");

    String value = (String) this.target.invoke(this.target, method, null);
    assertThat(value).isNotNull();
    verifyZeroInteractions(this.targetMethodHandler);
  }

  @Test
  void hashCode_isNotProxied() throws Throwable {
    Method method = ProxyTarget.class.getMethod("hashCode");

    int value = (int) this.target.invoke(this.target, method, null);
    assertThat(value).isNotZero();
    verifyZeroInteractions(this.targetMethodHandler);
  }

  @Test
  void equals_isNotProxied() throws Throwable {
    Method method = ProxyTarget.class.getMethod("equals", Object.class);

    boolean value = (boolean) this.target.invoke(this.target, method, new Object[]{"string"});
    assertThat(value).isFalse();
    verifyZeroInteractions(this.targetMethodHandler);
  }

  interface ProxyInterface {

    void test();

  }

}