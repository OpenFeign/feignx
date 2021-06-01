/*
 * Copyright 2019-2021 OpenFeign Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package feign.proxy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import feign.FeignConfiguration;
import feign.contract.TargetDefinition;
import feign.contract.TargetMethodDefinition;
import feign.TargetMethodHandler;
import feign.TargetMethodHandlerFactory;
import java.lang.reflect.Method;
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

  private ProxyTarget<ProxyInterface> target;

  @BeforeEach
  void setUp() {
    when(this.targetMethodHandlerFactory.create(any(TargetMethodDefinition.class),
        any(FeignConfiguration.class))).thenReturn(this.targetMethodHandler);

    TargetMethodDefinition targetMethodDefinition =
        TargetMethodDefinition.builder(ProxyInterface.class.getName())
            .name("test")
            .build();

    TargetDefinition definition = TargetDefinition.builder()
        .setTargetTypeName(ProxyInterface.class.getName())
        .setTargetPackageName(ProxyInterface.class.getPackageName())
        .setFullQualifiedTargetClassName(ProxyInterface.class.getName())
        .withTargetMethodDefinition(targetMethodDefinition)
        .build();

    this.target = new ProxyTarget<>(definition, this.targetMethodHandlerFactory, this.feignConfiguration);
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