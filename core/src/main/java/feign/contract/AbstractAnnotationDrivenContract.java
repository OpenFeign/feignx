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
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contract implementation that relies on Annotations to define the {@link TargetMethodDefinition}s
 * for the the methods on a given Target.
 */
public abstract class AbstractAnnotationDrivenContract implements Contract {

  private static final Logger logger =
      LoggerFactory.getLogger(AbstractAnnotationDrivenContract.class);

  private final Map<Class<? extends Annotation>, AnnotationProcessor<Annotation>>
      annotationProcessors = new LinkedHashMap<>();
  private final Map<Class<? extends Annotation>, ParameterAnnotationProcessor<Annotation>>
      parameterAnnotationProcessors = new LinkedHashMap<>();

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

  protected abstract Collection<Class<? extends Annotation>> getSupportedClassAnnotations();

  protected abstract Collection<Class<? extends Annotation>> getSupportedMethodAnnotations();

  protected abstract Collection<Class<? extends Annotation>> getSupportedParameterAnnotations();

  @SuppressWarnings("unchecked")
  protected <A extends Annotation> void registerAnnotationProcessor(
      Class<A> annotation, AnnotationProcessor<A> processor) {
    this.annotationProcessors
        .computeIfAbsent(annotation, annotationType -> (AnnotationProcessor<Annotation>) processor);
  }

  @SuppressWarnings("unchecked")
  protected <A extends Annotation> void registerParameterAnnotationProcessor(
      Class<A> annotation, ParameterAnnotationProcessor<A> processor) {
    this.parameterAnnotationProcessors
        .computeIfAbsent(annotation,
            annotationType -> (ParameterAnnotationProcessor<Annotation>) processor);
  }

  /**
   * Apply any Annotations located at the Type level.  Any definitions applied at this level will be
   * used as defaults for all methods on the target, unless redefined at the method or parameter
   * level.
   *
   * @param type    to inspect.
   * @param builder to store the applied configuration.
   */
  protected void processAnnotationsOnType(Class<?> type, TargetMethodDefinition.Builder builder) {
    /* get the list of annotations supported  */
    this.processAnnotations(type.getAnnotations(), this.getSupportedClassAnnotations(), builder);
  }

  /**
   * Apply any Annotations located at the Method level.
   *
   * @param method  to inspect
   * @param builder to store the applied configuration.
   */
  protected void processAnnotationsOnMethod(Class<?> type, Method method,
      TargetMethodDefinition.Builder builder) {
    /* set the common method information */
    builder.name(method.getName());
    builder.returnTypeDefinition(
        TypeDefinitionFactory.getInstance().create(method.getGenericReturnType(), type));
    this.processAnnotations(method.getAnnotations(), this.getSupportedMethodAnnotations(), builder);
  }

  /**
   * Apply any Annotations located at the Parameter level.
   *
   * @param parameter to inspect.
   * @param index     of the parameter in the method definition.
   * @param builder   to store the applied configuration.
   */
  protected void processAnnotationsOnParameter(Parameter parameter, Integer index,
      TargetMethodDefinition.Builder builder) {

    Annotation[] annotations = parameter.getAnnotations();
    Collection<Class<? extends Annotation>> supportedAnnotations = this
        .getSupportedParameterAnnotations();

    Arrays.stream(annotations)
        .filter(annotation -> supportedAnnotations.contains(annotation.annotationType()))
        .filter(annotation -> this.parameterAnnotationProcessors
            .containsKey(annotation.annotationType()))
        .forEach(annotation -> {
          ParameterAnnotationProcessor<Annotation> processor =
              this.parameterAnnotationProcessors.get(annotation.annotationType());
          processor.process(annotation, parameter.getName(), index, parameter.getType().getName(),
              builder);
        });
  }

  private void processAnnotations(Annotation[] annotations,
      Collection<Class<? extends Annotation>> supportedAnnotations,
      TargetMethodDefinition.Builder builder) {
    Arrays.stream(annotations)
        .filter(annotation -> supportedAnnotations.contains(annotation.annotationType()))
        .filter(annotation -> this.annotationProcessors.containsKey(annotation.annotationType()))
        .forEach(annotation -> {
          AnnotationProcessor<Annotation> processor = this.annotationProcessors
              .get(annotation.annotationType());
          processor.process(annotation, builder);
        });
  }

}
