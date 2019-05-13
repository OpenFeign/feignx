package feign.contract;

import feign.Contract;
import feign.Target;
import feign.TargetMethodDefinition;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contract implementation that relies on Annotations to define the {@link TargetMethodDefinition}s
 * for the the methods on a given Target.
 */
public abstract class AbstractAnnotationDrivenContract implements Contract {

  private static final Logger logger =
      LoggerFactory.getLogger(AbstractAnnotationDrivenContract.class);

  /**
   * Using the Contract annotations, process the Target and create the appropriate
   * {@link TargetMethodDefinition}s.
   *
   * @param target to apply this contract to.
   * @return a Collection of {@link TargetMethodDefinition}s with each methods configuration.
   */
  @Override
  public Collection<TargetMethodDefinition> apply(Target<?> target) {
    Set<TargetMethodDefinition> methods = new LinkedHashSet<>();

    /* special metadata object tha contains the class level configuration that will be
     * used by all methods on this target.
     */
    Class<?> targetType = target.type();
    TargetMethodDefinition root = new TargetMethodDefinition(target);
    logger.debug("Applying Contract to {}", targetType.getSimpleName());
    this.processAnnotationsOnType(targetType, root);

    for (Method method : targetType.getMethods()) {
      /* create a new metadata object from the root */
      TargetMethodDefinition methodMetadata = new TargetMethodDefinition(root);
      this.processAnnotationsOnMethod(targetType, method, methodMetadata);

      /* process method parameters */
      Parameter[] parameters = method.getParameters();
      for (int i = 0; i < parameters.length; i++) {
        Parameter parameter = parameters[i];
        this.processAnnotationsOnParameter(parameter, i, methodMetadata);
      }

      if (methodMetadata.getBody() == -1
          && parameters.length > methodMetadata.getTemplateParameters().size()) {
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
            methodMetadata.body(i);
            break;
          }
        }
      }

      if (!methodMetadata.isEmpty()) {
        methods.add(methodMetadata);
      }
    }
    logger.debug("Contract parsing completed.  Identified {} methods: [{}]",
        methods.size(), methods);
    return methods;
  }

  /**
   * Apply any Annotations located at the Type level.  Any definitions applied at this level will
   * be used as defaults for all methods on the target, unless redefined at the method or
   * parameter level.
   *
   * @param targetType to inspect.
   * @param targetMethodDefinition to store the applied configuration.
   */
  protected abstract void processAnnotationsOnType(Class<?> targetType,
      TargetMethodDefinition targetMethodDefinition);

  /**
   * Apply any Annotations located at the Method level.
   *
   * @param targetType to the method belongs to.
   * @param method to inspect
   * @param targetMethodDefinition to store the applied configuration.
   */
  protected abstract void processAnnotationsOnMethod(Class<?> targetType, Method method,
      TargetMethodDefinition targetMethodDefinition);

  /**
   * Apply any Annotations located at the Parameter level.
   *
   * @param parameter to inspect.
   * @param parameterIndex of the parameter in the method definition.
   * @param targetMethodDefinition to store the applied configuration.
   */
  protected abstract void processAnnotationsOnParameter(Parameter parameter, Integer parameterIndex,
      TargetMethodDefinition targetMethodDefinition);


}
