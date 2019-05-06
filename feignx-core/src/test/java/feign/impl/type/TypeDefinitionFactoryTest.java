package feign.impl.type;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TypeDefinitionFactoryTest {

  private TypeDefinitionFactory typeDefinitionFactory = TypeDefinitionFactory.getInstance();

  @Test
  void simpleType_isClassDefinition() throws Exception {
    Method method = Types.class.getMethod("value");
    TypeDefinition typeDefinition = this.typeDefinitionFactory.create(
        method.getGenericReturnType(), Types.class);

    assertThat(typeDefinition).isInstanceOf(ClassTypeDefinition.class);
    assertThat(typeDefinition.getType()).isAssignableFrom(String.class);
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
    Method method = Types.class.getMethod("array");
    TypeDefinition typeDefinition = this.typeDefinitionFactory.create(
        method.getGenericReturnType(), Types.class);

    assertThat(typeDefinition).isInstanceOf(GenericArrayTypeDefinition.class);
    assertThat(typeDefinition.getType()).isAssignableFrom(List.class);
  }

  @Test
  void complexInheritedTypeArray_isGenericArrayTypeWhenResolved() throws Exception {
    Method method = ExplicitTypes.class.getMethod("lists");
    TypeDefinition typeDefinition = this.typeDefinitionFactory.create(
        method.getGenericReturnType(), ExplicitTypes.class);

    assertThat(typeDefinition).isInstanceOf(GenericArrayTypeDefinition.class);
    assertThat(typeDefinition.getType()).isAssignableFrom(List.class);

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
  }

  /**
   * Interface with our simple types.
   */
  interface Types {

    String value();

    List<String> list();

    Map<String, Object> map();

    List<String>[] array();
  }

  /**
   * Base Interface that has multiple parameterized type variables.
   *
   * @param <I> input type.
   * @param <O> output type.
   */
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
   *
   * @param <O>
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
   *
   * @param <D>
   */
  interface LocalGenericTypes<D> extends LocalTypes {

    D localStuff();

  }

  /**
   * Simple Container Generic.
   * @param <D>
   */
  @SuppressWarnings("WeakerAccess")
  class Container<D> {

    D data;

    public D contained() {
      return data;
    }

  }

  /**
   * Container extension with the Type Variable defined.
   */
  private class StringContainer extends Container<String> {

  }

  /**
   * Extentions to our Container.
   */
  private class ExtendedStringContainer extends StringContainer {

  }

}