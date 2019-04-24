package feign.proxy;

import feign.AbstractTargetMethod;
import feign.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ProxyTargetMethod extends AbstractTargetMethod {

  private transient Type returnType;

  public ProxyTargetMethod(Target target, Method method) {
    super(getNameFromMethod(target, method));
  }

  @Override
  public Type returnType() {
    return this.returnType;
  }

  private static String getNameFromMethod(Target target, Method method) {
    StringBuilder sb = new StringBuilder()
        .append(target.type().getSimpleName())
        .append("#")
        .append(method.getName())
        .append("(");
    List<Type> parameters = Arrays.asList(method.getGenericParameterTypes());
    Iterator<Type> iterator = parameters.iterator();
    while(iterator.hasNext()) {
      Type parameter = iterator.next();
      sb.append(parameter.getTypeName());
      if (iterator.hasNext()) {
        sb.append(",");
      }
    }
    sb.append(")");
    return sb.toString();
  }

}
