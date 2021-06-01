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

package feign.impl.type;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;

class TypeDefinitionFactoryTest {

  private final TypeDefinitionFactory typeDefinitionFactory = TypeDefinitionFactory.getInstance();

  @Test
  void simpleType_isClassDefinition() throws Exception {
    Method method = Types.class.getMethod("value");
    TypeDefinition typeDefinition = this.typeDefinitionFactory.create(
        method.getGenericReturnType(), Types.class);

    assertThat(typeDefinition).isInstanceOf(ClassTypeDefinition.class);
    assertThat(typeDefinition.getType()).isAssignableFrom(String.class);
    assertThat(typeDefinition.isCollectionLike()).isFalse();
    assertThat(typeDefinition.isContainer()).isFalse();
    assertThat(typeDefinition.getActualType()).isAssignableFrom(String.class);
  }

  @Test
  void simpleCollection_isParameterizedDefinition() throws Exception {
    Method method = Types.class.getMethod("list");
    TypeDefinition typeDefinition = this.typeDefinitionFactory.create(
        method.getGenericReturnType(), Types.class);

    assertThat(typeDefinition).isInstanceOf(ParameterizedTypeDefinition.class);
    assertThat(typeDefinition.getType()).isAssignableFrom(List.class);
  }

  @Test
  void simpleMap_isParameterizedDefinition() throws Exception {
    Method method = Types.class.getMethod("map");
    TypeDefinition typeDefinition = this.typeDefinitionFactory.create(
        method.getGenericReturnType(), Types.class);

    assertThat(typeDefinition).isInstanceOf(ParameterizedTypeDefinition.class);
    assertThat(typeDefinition.getType()).isAssignableFrom(Map.class);
  }

  @Test
  void optional_isParameterizedDefinition_andContainer() throws Exception {
    Method method = Types.class.getMethod("wrapped");
    TypeDefinition typeDefinition = this.typeDefinitionFactory.create(
        method.getGenericReturnType(), Types.class);

    assertThat(typeDefinition).isInstanceOf(ParameterizedTypeDefinition.class);
    assertThat(typeDefinition.getType()).isAssignableFrom(Future.class);
    assertThat(typeDefinition.isCollectionLike()).isFalse();
    assertThat(typeDefinition.isContainer()).isTrue();
  }


  @Test
  void complexInheritedType_isParameterizedWithTypeArgumentsResolved() throws Exception {
    Method method = ExplicitTypes.class.getMethod("results");
    TypeDefinition typeDefinition = this.typeDefinitionFactory.create(
        method.getGenericReturnType(), ExplicitTypes.class);

    assertThat(typeDefinition).isInstanceOf(ParameterizedTypeDefinition.class);
    assertThat(typeDefinition.getType()).isAssignableFrom(Map.class);

    ParameterizedTypeDefinition parameterizedTypeDefinition =
        (ParameterizedTypeDefinition) typeDefinition;
    assertThat(parameterizedTypeDefinition.getActualTypeArguments()).isNotNull()
        .hasOnlyElementsOfType(ClassTypeDefinition.class);
  }

  @Test
  void containedParameterizedType_isParameterizedType() throws Exception {
    Method method = ExplicitTypes.class.getMethod("contained");
    TypeDefinition typeDefinition = this.typeDefinitionFactory.create(
        method.getGenericReturnType(), ExplicitTypes.class);
    assertThat(typeDefinition).isInstanceOf(ParameterizedTypeDefinition.class);
    assertThat(typeDefinition.getType()).isAssignableFrom(Collection.class);

    ParameterizedTypeDefinition parameterizedTypeDefinition =
        (ParameterizedTypeDefinition) typeDefinition;
    assertThat(parameterizedTypeDefinition.getActualTypeArguments()).isNotNull()
        .hasOnlyElementsOfType(ParameterizedTypeDefinition.class);
  }

  @Test
  void complexMultipleInheritedType_isParameterizedWithTypeArgumentsResolved() throws Exception {
    Method method = ExplicitTypes.class.getMethod("results");
    TypeDefinition typeDefinition = this.typeDefinitionFactory.create(
        method.getGenericReturnType(), ExtendedExplicitTypes.class);

    assertThat(typeDefinition).isInstanceOf(ParameterizedTypeDefinition.class);
    assertThat(typeDefinition.getType()).isAssignableFrom(Map.class);

    ParameterizedTypeDefinition parameterizedTypeDefinition =
        (ParameterizedTypeDefinition) typeDefinition;
    assertThat(parameterizedTypeDefinition.getActualTypeArguments()).isNotNull()
        .hasOnlyElementsOfType(ClassTypeDefinition.class);

    TypeDefinition[] typeDefinitions =
        (TypeDefinition[]) parameterizedTypeDefinition.getActualTypeArguments();
    assertThat(typeDefinitions[0]).isInstanceOf(ClassTypeDefinition.class);
    assertThat(typeDefinitions[0].getType()).isAssignableFrom(Number.class);
    assertThat(typeDefinitions[1]).isInstanceOf(ClassTypeDefinition.class);
    assertThat(typeDefinitions[1].getType()).isAssignableFrom(String.class);
  }

  @Test
  void arrayType_isGenericArrayType() throws Exception {
    Method method = Types.class.getMethod("collectionArray");
    TypeDefinition typeDefinition = this.typeDefinitionFactory.create(
        method.getGenericReturnType(), Types.class);

    assertThat(typeDefinition).isInstanceOf(GenericArrayTypeDefinition.class);
    assertThat(typeDefinition.getType()).isAssignableFrom(List.class);
    assertThat(typeDefinition.isCollectionLike()).isTrue();
  }


  @Test
  void complexInheritedTypeArray_isGenericArrayTypeWhenResolved() throws Exception {
    Method method = ExplicitTypes.class.getMethod("lists");
    TypeDefinition typeDefinition = this.typeDefinitionFactory.create(
        method.getGenericReturnType(), ExplicitTypes.class);

    assertThat(typeDefinition).isInstanceOf(GenericArrayTypeDefinition.class);
    assertThat(typeDefinition.getType()).isAssignableFrom(List.class);
    assertThat(typeDefinition.isCollectionLike()).isTrue();

    GenericArrayTypeDefinition genericArrayTypeDefinition =
        (GenericArrayTypeDefinition) typeDefinition;
    assertThat(genericArrayTypeDefinition.getGenericComponentType())
        .isInstanceOf(ParameterizedType.class);

    ParameterizedTypeDefinition parameterizedTypeDefinition =
        (ParameterizedTypeDefinition) genericArrayTypeDefinition.getGenericComponentType();
    assertThat(parameterizedTypeDefinition.getActualTypeArguments()).isNotNull()
        .hasOnlyElementsOfType(ClassTypeDefinition.class);

    TypeDefinition[] classTypeDefinitions =
        (TypeDefinition[]) parameterizedTypeDefinition.getActualTypeArguments();
    assertThat(classTypeDefinitions).allMatch(
        classTypeDefinition -> classTypeDefinition.getType().isAssignableFrom(String.class));
  }

  @Test
  void complexInheritedWildCardTypeUpper_isWildCardTypeDefinition() throws Exception {
    Method method = ExplicitTypes.class.getMethod("wildcards");
    TypeDefinition typeDefinition = this.typeDefinitionFactory.create(
        method.getGenericReturnType(), ExplicitTypes.class);

    assertThat(typeDefinition).isInstanceOf(ParameterizedTypeDefinition.class);

    ParameterizedTypeDefinition parameterizedTypeDefinition =
        (ParameterizedTypeDefinition) typeDefinition;
    assertThat(parameterizedTypeDefinition.getActualTypeArguments()).isNotNull()
        .hasAtLeastOneElementOfType(WildCardTypeDefinition.class);

    TypeDefinition[] typeDefinitions =
        (TypeDefinition[]) parameterizedTypeDefinition.getActualTypeArguments();
    for (TypeDefinition definition : typeDefinitions) {
      if (definition instanceof WildCardTypeDefinition) {
        /* verify that the upper bounds match the interface */
        assertThat(definition.getType()).isAssignableFrom(Number.class);
        assertThat(((WildCardTypeDefinition) definition).getUpperBounds()).isNotEmpty();
        assertThat(((WildCardTypeDefinition) definition).getLowerBounds()).isEmpty();
      }
    }
  }

  @Test
  void complexInheritedWildCardTypeLower_isWildCardTypeDefinition() throws Exception {
    Method method = ExplicitTypes.class.getMethod("supers");
    TypeDefinition typeDefinition = this.typeDefinitionFactory.create(
        method.getGenericReturnType(), ExplicitTypes.class);

    assertThat(typeDefinition).isInstanceOf(ParameterizedTypeDefinition.class);

    ParameterizedTypeDefinition parameterizedTypeDefinition =
        (ParameterizedTypeDefinition) typeDefinition;
    assertThat(parameterizedTypeDefinition.getActualTypeArguments()).isNotNull()
        .hasAtLeastOneElementOfType(WildCardTypeDefinition.class);

    TypeDefinition[] typeDefinitions =
        (TypeDefinition[]) parameterizedTypeDefinition.getActualTypeArguments();
    for (TypeDefinition definition : typeDefinitions) {
      if (definition instanceof WildCardTypeDefinition) {
        /* verify that the upper bounds match the interface */
        assertThat(definition.getType()).isAssignableFrom(Set.class);
        assertThat(((WildCardTypeDefinition) definition).getUpperBounds()).isNotEmpty();
        assertThat(((WildCardTypeDefinition) definition).getLowerBounds()).isNotEmpty();
      }
    }
  }

  @Test
  void typeVariableDefinedAtMethod_isNotResolved() throws Exception {
    Method method = ExplicitTypes.class.getMethod("undefined");
    TypeDefinition typeDefinition = this.typeDefinitionFactory.create(
        method.getGenericReturnType(), ExplicitTypes.class);

    assertThat(typeDefinition).isNull();
  }

  @Test
  void typeVariableExplicitReturn_isClassTypeDefinition() throws Exception {
    Method method = ExplicitTypes.class.getMethod("input");
    TypeDefinition typeDefinition = this.typeDefinitionFactory.create(
        method.getGenericReturnType(), ExplicitTypes.class);

    assertThat(typeDefinition).isInstanceOf(ClassTypeDefinition.class);
    assertThat(typeDefinition.getType()).isAssignableFrom(String.class);
  }

  @Test
  void typeVariableLocallyDefined_isClassDefinition() throws Exception {
    Method method = LocalGenericTypes.class.getMethod("localStuff");
    TypeDefinition typeDefinition = this.typeDefinitionFactory.create(
        method.getGenericReturnType(), ExplicitTypes.class);

    assertThat(typeDefinition).isInstanceOf(ClassTypeDefinition.class);
  }

  @Test
  void typeVariableConcreteType_isClassDefinition() throws Exception {
    Method method = StringContainer.class.getMethod("contained");
    TypeDefinition typeDefinition = this.typeDefinitionFactory.create(
        method.getGenericReturnType(), StringContainer.class);

    assertThat(typeDefinition).isInstanceOf(ClassTypeDefinition.class);
    assertThat(typeDefinition.getType()).isAssignableFrom(String.class);
  }

  @Test
  void complexInheritanceWithTypeVariable_inAConcreteType_isClassDefinition() throws Exception {
    Method method = ExtendedStringContainer.class.getMethod("contained");
    TypeDefinition typeDefinition = this.typeDefinitionFactory.create(
        method.getGenericReturnType(), ExtendedStringContainer.class);

    assertThat(typeDefinition).isInstanceOf(ClassTypeDefinition.class);
    assertThat(typeDefinition.getType()).isAssignableFrom(String.class);
    assertThat(typeDefinition.isCollectionLike()).isFalse();
    assertThat(typeDefinition.isContainer()).isFalse();
  }

  @Test
  void array_isCollectionLike() throws Exception {
    Method method = Types.class.getMethod("array");
    TypeDefinition typeDefinition = this.typeDefinitionFactory.create(
        method.getGenericReturnType(), Types.class);
    assertThat(typeDefinition.isCollectionLike()).isTrue();
  }

  @Test
  void collection_isCollectionLike() throws Exception {
    Method method = Types.class.getMethod("collection");
    TypeDefinition typeDefinition = this.typeDefinitionFactory.create(
        method.getGenericReturnType(), Types.class);
    assertThat(typeDefinition.isCollectionLike()).isTrue();
  }

  @Test
  void iterable_isCollectionLike() throws Exception {
    Method method = Types.class.getMethod("iterable");
    TypeDefinition typeDefinition = this.typeDefinitionFactory.create(
        method.getGenericReturnType(), Types.class);
    assertThat(typeDefinition.isCollectionLike()).isTrue();
  }

  @Test
  void parameterizedType_withOwner_isResolved() throws Exception {
    Method method = Outside.class.getMethod("outside");
    TypeDefinition typeDefinition = this.typeDefinitionFactory.create(
        method.getGenericReturnType(), Outside.class);
    assertThat(typeDefinition).isInstanceOf(ParameterizedTypeDefinition.class);
    assertThat(((ParameterizedTypeDefinition) typeDefinition).getOwnerType()).isNotNull();
  }

  /**
   * Interface with our simple types.
   */
  interface Types {

    String value();

    List<String> list();

    Map<String, Object> map();

    List<String>[] collectionArray();

    Future<String> wrapped();

    String[] array();

    Collection<String> collection();

    Iterable<String> iterable();
  }

  /**
   * Base Interface that has multiple parameterized type variables.
   *
   * @param <I> input type.
   * @param <O> output type.
   */
  @SuppressWarnings("rawtypes")
  interface GenericTypes<I, O> {

    Map<I, O> results();

    List<I>[] lists();

    Map<? extends Number, O> wildcards();

    Map<? super Set, O> supers();

    <T> T undefined();

    I input();

    Collection<List<O>> contained();
  }

  /**
   * Extension of the Generic interface with the type variables defined.
   */
  interface ExplicitTypes extends GenericTypes<String, String> {

  }

  /**
   * Secondary Extension of Generic Types.
   */
  interface ExtendedGenericTypes<O> extends GenericTypes<Number, O> {

  }

  /**
   * Final Extension of the Generic Types interface.
   */
  interface ExtendedExplicitTypes extends ExtendedGenericTypes<String> {

  }


  /**
   * Interface without Type Variables.
   */
  interface LocalTypes {

  }

  /**
   * Generic Type Interface where the Type Variable is locally defined.
   */
  interface LocalGenericTypes<D> extends LocalTypes {

    D localStuff();

  }

  /**
   * Simple Container Generic.
   */
  @SuppressWarnings("WeakerAccess")
  static class Container<D> {

    D data;

    public D contained() {
      return data;
    }

  }

  /**
   * Container extension with the Type Variable defined.
   */
  private static class StringContainer extends Container<String> {

  }

  /**
   * Extensions to our Container.
   */
  private static class ExtendedStringContainer extends StringContainer {

  }

  /**
   * Owned Type.
   */
  @SuppressWarnings("unused")
  interface Outside<O> {

    Inside<O> outside();

    interface Inside<I> {

      I inside();

    }
  }
}