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

package feign.contract;

import feign.Contract;
import feign.Target;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

/**
 * Definition for a Target interface.  Used by {@link Contract} implementations to create new {@link
 * Target} instances.
 */
@Immutable
@ThreadSafe
public final class TargetDefinition {

  private final String targetPackageName;
  private final String targetTypeName;
  private final String fullyQualifiedTargetClassName;
  private final Set<String> extensions;
  private final Set<String> implementations;
  private final Collection<TargetMethodDefinition> methodDefinitions;

  public static TargetDefinitionBuilder builder() {
    return new TargetDefinitionBuilder();
  }

  /**
   * Create a new {@link TargetDefinition}.
   *
   * @param builder with the {@link TargetDefinition} properties.
   */
  private TargetDefinition(TargetDefinitionBuilder builder) {
    this.targetPackageName = builder.targetPackageName;
    this.targetTypeName = builder.targetTypeName;
    this.fullyQualifiedTargetClassName = builder.fullyQualifiedTargetClassName;
    this.extensions = Set.copyOf(builder.extensions);
    this.implementations = Set.copyOf(builder.implementations);
    this.methodDefinitions = Set.copyOf(builder.methodDefinitions);
  }

  public String getTargetPackageName() {
    return targetPackageName;
  }

  public String getTargetTypeName() {
    return targetTypeName;
  }

  public String getFullyQualifiedTargetClassName() {
    return this.fullyQualifiedTargetClassName;
  }

  public Set<String> getExtensions() {
    return extensions;
  }

  public Set<String> getImplementations() {
    return implementations;
  }

  public Collection<TargetMethodDefinition> getMethodDefinitions() {
    return methodDefinitions;
  }

  /**
   * {@link TargetDefinition} builder.
   */
  @SuppressWarnings("UnusedReturnValue")
  @ThreadSafe
  public static class TargetDefinitionBuilder {

    private String targetPackageName;
    private String targetTypeName;
    private String fullyQualifiedTargetClassName;
    private final Set<String> extensions = new CopyOnWriteArraySet<>();
    private final Set<String> implementations = new CopyOnWriteArraySet<>();
    private final Collection<TargetMethodDefinition> methodDefinitions =
        new CopyOnWriteArraySet<>();

    private TargetDefinitionBuilder() {
      super();
    }

    /**
     * The fully qualified class name of the Target interface.
     *
     * @param targetClassName of the target.
     * @return a builder instance for chaining.
     */
    public TargetDefinitionBuilder setFullQualifiedTargetClassName(String targetClassName) {
      this.fullyQualifiedTargetClassName = targetClassName;
      return this;
    }

    /**
     * The fully qualified package name where the Target interface resided.  Ex {@code my.package}
     *
     * @param targetPackageName of the Target.
     * @return a builder instance for chaining.
     */
    public TargetDefinitionBuilder setTargetPackageName(String targetPackageName) {
      this.targetPackageName = targetPackageName;
      return this;
    }

    /**
     * The simple type name of the Target interface.  Ex: {@code MyInterface}.
     *
     * @param targetTypeName of the Target.
     * @return a builder instance for chaining.
     */
    public TargetDefinitionBuilder setTargetTypeName(String targetTypeName) {
      this.targetTypeName = targetTypeName;
      return this;
    }

    /**
     * The fully qualified class name of any additional interfaces a Target extends that also are
     * Feign Targets.
     *
     * @param fqdnSuperclassName of the extended interface.
     * @return a builder instance for chaining.
     */
    public TargetDefinitionBuilder withExtension(String fqdnSuperclassName) {
      this.extensions.add(fqdnSuperclassName);
      return this;
    }

    /**
     * The fully qualified class name of any additional interfaces a Target extends that are
     * <b>not Feign Targets.</b>.
     *
     * @param fqdnInterfaceName of the extended interface.
     * @return a builder instance for chaining.
     */
    public TargetDefinitionBuilder withImplementation(String fqdnInterfaceName) {
      this.implementations.add(fqdnInterfaceName);
      return this;
    }

    /**
     * A {@link TargetMethodDefinition} from the Target interface.
     *
     * @param methodDefinition instance.
     * @return a builder instance for chaining.
     */
    public TargetDefinitionBuilder withTargetMethodDefinition(
        TargetMethodDefinition methodDefinition) {
      this.methodDefinitions.add(methodDefinition);
      return this;
    }

    /**
     * Create a new {@link TargetDefinition}.
     *
     * @return a new {@link TargetDefinition} instance.
     */
    public TargetDefinition build() {
      return new TargetDefinition(this);
    }
  }


}
