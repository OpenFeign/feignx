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
import feign.FeignConfiguration;
import feign.contract.TargetDefinition.TargetDefinitionBuilder;
import feign.impl.type.TypeDefinitionFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contract implementation that relies on Annotations to define the {@link TargetMethodDefinition}s
 * for the the methods on a given Target.
 */
public abstract class AbstractAnnotationDrivenContract implements Contract {

  private static final Logger logger =
      LoggerFactory.getLogger(AbstractAnnotationDrivenContract.class);

  protected final TypeDefinitionFactory typeDefinitionFactory = new TypeDefinitionFactory();

  /**
   * Processes the {@link FeignConfiguration} and generate a new {@link TargetDefinition} instance.
   *
   * @param targetType with the Target configuration.
   * @return a new {@link TargetDefinition} instance.
   */
  @Override
  public TargetDefinition apply(Class<?> targetType, FeignConfiguration configuration) {

    TargetDefinitionBuilder builder = TargetDefinition.builder()
        .setTargetPackageName(targetType.getPackageName())
        .setFullQualifiedTargetClassName(targetType.getName())
        .setTargetTypeName(targetType.getSimpleName());

    for (Class<?> extension : targetType.getInterfaces()) {
      builder.withExtension(extension.getCanonicalName());
    }

    /* create the default method metadata from any annotations on the interface itself */
    TargetMethodDefinition.Builder rootMethodBuilder = TargetMethodDefinition
        .builder(targetType.getName())
        .target(configuration.getTarget());
    this.processAnnotationsOnType(targetType, rootMethodBuilder);
    TargetMethodDefinition root = rootMethodBuilder.build();

    for (Method method : targetType.getMethods()) {
      /* create a new metadata object from the root */
      TargetMethodDefinition.Builder methodBuilder = TargetMethodDefinition.from(root);
      this.processAnnotationsOnMethod(targetType, method, methodBuilder);

      /* process method parameters */
      Parameter[] parameters = method.getParameters();
      for (int i = 0; i < parameters.length; i++) {
        Parameter parameter = parameters[i];
        this.processAnnotationsOnParameter(parameter, i, methodBuilder);
      }

      /* build the instance */
      TargetMethodDefinition methodMetadata = methodBuilder.build();

      /* determine if implicit body parameter identification is required */
      if (methodMetadata.getBody() == -1
          && parameters.length > methodMetadata.getParameterDefinitions().size()) {
        /* there are parameters on this method that are not registered.  in these cases, we
         * allow users to define which parameter they want as the Request Body without an explicit
         * annotation, look for that parameter and register it.
         */
        logger.debug(
            "No explicit Request Body parameter found on Method: {}, using first parameter without"
                + " an annotation, if present.", method.getName());
        for (int i = 0; i < parameters.length; i++) {
          Parameter parameter = parameters[i];
          if (parameter.getAnnotations().length == 0) {
            /* assume this is our body */
            logger.debug("Marking Parameter {}:{} as the Request Body.",
                parameter.getName(), parameter.getType().getSimpleName());

            /* update the builder and build again */
            methodBuilder.body(i);
            methodMetadata = methodBuilder.build();
            break;
          }
        }
      }

      if (!methodMetadata.isEmpty()) {
        builder.withTargetMethodDefinition(methodMetadata);
      }
    }

    return builder.build();
  }

  /**
   * Apply any Annotations located at the Type level.  Any definitions applied at this level will be
   * used as defaults for all methods on the target, unless redefined at the method or parameter
   * level.
   *
   * @param targetType to inspect.
   * @param targetMethodDefinitionBuilder to store the applied configuration.
   */
  protected abstract void processAnnotationsOnType(Class<?> targetType,
      TargetMethodDefinition.Builder targetMethodDefinitionBuilder);

  /**
   * Apply any Annotations located at the Method level.
   *
   * @param targetType to the method belongs to.
   * @param method to inspect
   * @param targetMethodDefinitionBuilder to store the applied configuration.
   */
  protected abstract void processAnnotationsOnMethod(Class<?> targetType, Method method,
      TargetMethodDefinition.Builder targetMethodDefinitionBuilder);

  /**
   * Apply any Annotations located at the Parameter level.
   *
   * @param parameter to inspect.
   * @param parameterIndex of the parameter in the method definition.
   * @param targetMethodDefinitionBuilder to store the applied configuration.
   */
  protected abstract void processAnnotationsOnParameter(Parameter parameter, Integer parameterIndex,
      TargetMethodDefinition.Builder targetMethodDefinitionBuilder);

}
