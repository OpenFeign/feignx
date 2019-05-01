package feign.contract;

import feign.Contract;
import feign.Target;
import feign.TargetMethodDefinition;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractAnnotationDrivenContract implements Contract {

  @Override
  public Collection<TargetMethodDefinition> apply(Target<?> target) {
    Set<TargetMethodDefinition> methods = new LinkedHashSet<>();

    /* special metadata object tha contains the class level configuration that will be
     * used by all methods on this target.
     */
    Class<?> targetType = target.type();
    TargetMethodDefinition root = new TargetMethodDefinition(target);
    this.processAnnotationsOnType(targetType, root);

    for (Method method : targetType.getMethods()) {
      /* create a new metadata object from the root */
      TargetMethodDefinition methodMetadata = new TargetMethodDefinition(root);
      this.processAnnotationsOnMethod(targetType, method, methodMetadata);

      /* process method parameters */
      Parameter[] parameters = method.getParameters();
      for (int i = 0; i < parameters.length; i ++) {
        Parameter parameter = parameters[i];
        this.processAnnotationsOnParameter(parameter, i, methodMetadata);
      }

      if (methodMetadata.getBody() == -1) {
        /* no explicit @Body parameter defined, look for the first parameter that
         * does not have an annotation and register it as the body.
         */
        for (int i = 0; i < parameters.length; i ++) {
          Parameter parameter = parameters[i];
          if (parameter.getAnnotations().length == 0) {
            /* assume this is our body */
            methodMetadata.body(i);
            break;
          }
        }
      }

      if (!methodMetadata.isEmpty()) {
        methods.add(methodMetadata);
      }
    }

    return methods;
  }

  protected abstract void processAnnotationsOnType(Class<?> targetType, TargetMethodDefinition targetMethodDefinition);

  protected abstract void processAnnotationsOnMethod(Class<?> targetType, Method method, TargetMethodDefinition targetMethodDefinition);

  protected abstract void processAnnotationsOnParameter(Parameter parameter, Integer parameterIndex, TargetMethodDefinition targetMethodDefinition);


}
