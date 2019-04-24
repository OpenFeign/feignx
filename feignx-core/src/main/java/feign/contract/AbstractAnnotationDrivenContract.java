package feign.contract;

import feign.Contract;
import feign.Target;
import feign.impl.TargetMethodMetadata;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractAnnotationDrivenContract implements Contract {

  @Override
  public Collection<TargetMethodMetadata> apply(Target<?> target) {
    Set<TargetMethodMetadata> methods = new LinkedHashSet<>();

    /* special metadata object tha contains the class level configuration that will be
     * used by all methods on this target.
     */
    Class<?> targetType = target.type();
    TargetMethodMetadata root = new TargetMethodMetadata();
    this.processAnnotationsOnType(targetType, root);

    for (Method method : targetType.getMethods()) {
      /* create a new metadata object from the root */
      TargetMethodMetadata methodMetadata = new TargetMethodMetadata(root);
      this.processAnnotationsOnMethod(method, methodMetadata);

      /* process method parameters */
      Parameter[] parameters = method.getParameters();
      for (int i = 0; i < parameters.length; i ++) {
        Parameter parameter = parameters[i];
        this.processAnnotationsOnParameter(parameter, i, methodMetadata);
      }
      methods.add(methodMetadata);
    }

    return methods;
  }

  protected abstract void processAnnotationsOnType(Class<?> type, TargetMethodMetadata targetMethodMetadata);

  protected abstract void processAnnotationsOnMethod(Method method, TargetMethodMetadata targetMethodMetadata);

  protected abstract void processAnnotationsOnParameter(Parameter parameter, Integer parameterIndex, TargetMethodMetadata targetMethodMetadata);


}
