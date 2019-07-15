/*
 * Copyright 2019 OpenFeign Contributors
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

package feign.template.expander;

import feign.support.StringUtils;
import feign.template.ExpanderRegistry;
import feign.template.ExpressionExpander;
import feign.template.ExpressionVariable;
import feign.template.Expressions;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Expression Expander that is responsible for handling complex objects that adhere
 * to the <a href="https://www.oracle.com/technetwork/articles/javaee/spec-136004.html">Java Bean Specification</a>.
 * <p>
 *   This expander will use the Introspector to identify any accessor methods on the the value
 *   being expanded.  The result of the expansion will be the same as an associative array
 *   per RFC 6570, honoring all explode, delimiter, and prefix modifiers.
 * </p>
 * <p>
 *   Beans that contains Lists, Maps and other nested object will be expanded using the appropriate
 *   {@link ExpressionExpander}, with the following limitations:
 * </p>
 * <ul>
 *   <li>
 *     Lists and Maps will be expanded in their non-exploded forms.
 *   </li>
 *   <li>
 *     Lists and Maps that contain nested object will be exploded using the rules defined
 *     by the {@link ListExpander} and {@link MapExpander} respectively, and not this expander.
 *   </li>
 *   <li>
 *     Nested objects will be expanded prefixing their property names with the contained
 *     property name.  ex: parent.nested = value.
 *   </li>
 * </ul>
 */
public class BeanExpander extends MapExpander {

  private static BeanExpander instance;
  private ExpanderRegistry expanderRegistry;

  /**
   * Returns a Singleton BeanExpander instance.
   *
   * @param expanderRegistry to use.
   * @return a BeanExpander instance.
   */
  static BeanExpander getInstance(ExpanderRegistry expanderRegistry) {
    if (instance == null) {
      instance = new BeanExpander(expanderRegistry);
    }
    return instance;
  }

  /**
   * Creates a new BeanExpander.
   *
   * @param expanderRegistry to use when expanding simple object properties.
   */
  BeanExpander(ExpanderRegistry expanderRegistry) {
    this.expanderRegistry = expanderRegistry;
  }

  /**
   * Expands the given value, which must be a simple Java Bean, by creating a Map of the
   * accessible properties and delegating to the {@link MapExpander}.
   *
   * @param variable to expand.
   * @param value containing the variable values.
   * @return the expanded bean.
   */
  @Override
  public String expand(ExpressionVariable variable, Object value) {
    Map<String, Object> beanValueMap;
    try {
      beanValueMap = new LinkedHashMap<>(this.expandBean(variable, null, value));
    } catch (Exception ex) {
      throw new IllegalStateException("Error occurred expanding Bean "
          + value.getClass().getSimpleName() + ".  " + ex.getMessage(), ex);
    }
    return super.expand(variable, beanValueMap);
  }

  /**
   * Expand the provided Bean using an {@link Introspector} from the Java Beans API.  Each
   * property with a valid read method will be read and added to a map, keyed by the property name.
   * If the bean contains nested properties, the nested object will be expanded using the
   * contained objects name as the property key prefix.
   * <p>
   *   ex: parent.nested = value
   * </p>
   *
   * @param variable being expanded.
   * @param parent of the bean provided, can be {@literal null}
   * @param bean containing the values for the variable.
   * @return a Map containing the bean property names and values.
   * @throws Exception if an error occurred during expansion.
   */
  private Map<String, Object> expandBean(
      ExpressionVariable variable, String parent, Object bean) throws Exception {
    Map<String, Object> beanPropertyMap = new TreeMap<>();
    BeanInfo info = Introspector.getBeanInfo(bean.getClass());
    PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
    for (PropertyDescriptor propertyDescriptor : descriptors) {
      String name = propertyDescriptor.getName();

      /* class is a special property, we must ignore it or we end up with some very strange
       * output.
       */
      if (!"class".equalsIgnoreCase(name)) {
        if (StringUtils.isNotEmpty(parent)) {
          /* nested objects use dot notation to indicate hierarchy */
          name = parent + "." + name;
        }

        /* only properties with read methods are evaluated.  this allows users to manage property
         * accessibility through methods instead of direct field access.  while direct field access
         * is faster, this method provides the most flexibility and allows for logic on the
         * bean side to be run before expansion.
         */
        Method readMethod = propertyDescriptor.getReadMethod();
        if (readMethod != null) {
          /* obtain the value */
          Object result = readMethod.invoke(bean);
          if (result != null) {
            if (ExpanderUtils.isSimpleType(result.getClass())) {
              /* use the expander registry to obtain an instance of the appropriate expander
               * and delegate
               */
              ExpressionExpander expander =
                  this.expanderRegistry.getExpanderByType(result.getClass());

              /* we need to create a new variable so the expander can process the result
               * in the correct context.
               */
              ExpressionVariable propertyVariable =
                  new ExpressionVariable(variable.getPrefix(), name, variable.isExploded(),
                      Expressions.create("{" + name + "}"));
              String expanded = expander.expand(propertyVariable, result);
              beanPropertyMap.put(name, expanded);
            } else {
              /* we have a nested object, expand it */
              beanPropertyMap.putAll(this.expandBean(variable, name, result));
            }
          }
        }
      }
    }
    return beanPropertyMap;
  }
}
