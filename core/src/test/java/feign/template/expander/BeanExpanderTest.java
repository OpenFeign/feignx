/*
 * Copyright 2019-2020 OpenFeign Contributors
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import feign.template.ExpanderRegistry;
import feign.template.Expression;
import feign.template.ExpressionVariable;
import feign.template.Expressions;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class BeanExpanderTest {

  @Test
  void simpleObject_expandLikeMap_withUndefinedValue_Missing() {
    Address address = new Address();
    address.setStreetNumber("121B");
    address.setStreetName("Baker St.");
    address.setTown("London");
    address.setCountry("England");
    address.setGivenName("Sherlock");
    address.setSurname("Holmes");

    ExpressionVariable expressionVariable = mock(ExpressionVariable.class);
    Expression expression = Expressions.create("{?address*}");

    when(expressionVariable.getName()).thenReturn("address");
    when(expressionVariable.getPrefix()).thenReturn(0);
    when(expressionVariable.getExpression()).thenReturn(expression);
    when(expressionVariable.isExploded()).thenReturn(true);

    BeanExpander expander = new BeanExpander(new CachingExpanderRegistry());
    String expanded = expander.expand(expressionVariable, address);
    assertThat(expanded).isEqualToIgnoringCase("country=England&givenName=Sherlock&"
        + "streetName=Baker%20St.&streetNumber=121B&surname=Holmes"
        + "&town=London");
  }

  @Test
  void nestedObjects_expandWith_dotNotation() {
    Post post = new Post("Title", "Summary");
    post.setCategory("sports");
    post.addTag("hockey");
    post.addTag("ice");
    post.addTag("Canada");

    BeanExpander expander = new BeanExpander(new CachingExpanderRegistry());
    ExpressionVariable expressionVariable = mock(ExpressionVariable.class);
    Expression expression = Expressions.create("{?post*}");

    when(expressionVariable.getName()).thenReturn("post");
    when(expressionVariable.getPrefix()).thenReturn(0);
    when(expressionVariable.getExpression()).thenReturn(expression);
    when(expressionVariable.isExploded()).thenReturn(true);

    String expanded = expander.expand(expressionVariable, post);
    assertThat(expanded).isEqualToIgnoringCase(
        "category.label=sports&category.name=sports&summary=Summary&tags=hockey%2Cice%2CCanada&title=Title");
  }

  @Test
  void property_withoutReadMethod_isSkipped() {
    Post post = new Post("Title", "Summary");
    post.setCategory("sports");
    post.addTag("hockey");
    post.addTag("ice");
    post.addTag("Canada");
    post.setPublished(LocalDateTime.now());

    BeanExpander expander = new BeanExpander(new CachingExpanderRegistry());
    ExpressionVariable expressionVariable = mock(ExpressionVariable.class);
    Expression expression = Expressions.create("{?post*}");

    when(expressionVariable.getName()).thenReturn("post");
    when(expressionVariable.getPrefix()).thenReturn(0);
    when(expressionVariable.getExpression()).thenReturn(expression);
    when(expressionVariable.isExploded()).thenReturn(true);

    String expanded = expander.expand(expressionVariable, post);
    assertThat(expanded).isEqualToIgnoringCase(
        "category.label=sports&category.name=sports&summary=Summary&tags=hockey%2Cice%2CCanada&title=Title");
  }

  @Test
  void singletonCreation_shouldLazyLoad_andReuse() {
    BeanExpander beanExpander = BeanExpander.getInstance(mock(ExpanderRegistry.class));
    assertThat(beanExpander).isNotNull();

    BeanExpander another = BeanExpander.getInstance(mock(ExpanderRegistry.class));
    assertThat(another).isEqualTo(beanExpander);
  }

  public class Address {
    private String givenName;
    private String surname;
    private String streetNumber;
    private String streetName;
    private String streetType;
    private String floor;
    private String town;
    private String region;
    private String postalCode;
    private String country;

    public Address() {
      super();
    }

    public String getGivenName() {
      return givenName;
    }

    public void setGivenName(String givenName) {
      this.givenName = givenName;
    }

    public String getSurname() {
      return surname;
    }

    public void setSurname(String surname) {
      this.surname = surname;
    }

    public String getStreetNumber() {
      return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
      this.streetNumber = streetNumber;
    }

    public String getStreetName() {
      return streetName;
    }

    public void setStreetName(String streetName) {
      this.streetName = streetName;
    }

    public String getStreetType() {
      return streetType;
    }

    public void setStreetType(String streetType) {
      this.streetType = streetType;
    }

    public String getFloor() {
      return floor;
    }

    public void setFloor(String floor) {
      this.floor = floor;
    }

    public String getTown() {
      return town;
    }

    public void setTown(String town) {
      this.town = town;
    }

    public String getRegion() {
      return region;
    }

    public void setRegion(String region) {
      this.region = region;
    }

    public String getPostalCode() {
      return postalCode;
    }

    public void setPostalCode(String postalCode) {
      this.postalCode = postalCode;
    }

    public String getCountry() {
      return country;
    }

    public void setCountry(String country) {
      this.country = country;
    }
  }

  public class Post {
    private String title;
    private String summary;
    private Category category;
    private List<Tag> tags = new ArrayList<>();
    private LocalDateTime published;

    public Post() {
      super();
    }

    public Post(String title, String summary) {
      this.title = title;
      this.summary = summary;
    }

    public String getTitle() {
      return title;
    }

    public String getSummary() {
      return summary;
    }

    public void setCategory(String category) {
      this.category = new Category(category);
    }

    public Category getCategory() {
      return this.category;
    }

    public void addTag(String tag) {
      this.tags.add(new Tag(tag));
    }

    public List<Tag> getTags() {
      return this.tags;
    }

    public void setPublished(LocalDateTime published) {
      this.published = published;
    }
  }

  public class Tag {
    private String name;

    public Tag() {
      super();
    }

    public Tag(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return this.getName();
    }
  }

  public class Category {
    private String scheme;
    private String name;
    private String label;

    public Category() {
      super();
    }

    public Category(String name) {
      this.label = name;
      this.name = name;
    }

    public String getScheme() {
      return scheme;
    }

    public void setScheme(String scheme) {
      this.scheme = scheme;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getLabel() {
      return label;
    }

    public void setLabel(String label) {
      this.label = label;
    }
  }

  public class BadBean {
    private String name;
    private List<BadProperty> props = new ArrayList<>();

    public BadBean() {
      super();
      props.add(new BadProperty());
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public List<BadProperty> getProps() {
      return props;
    }

    public void setProps(List<BadProperty> props) {
      this.props = props;
    }
  }

  public class BadProperty {
    public BadProperty() {
      super();
    }

    @Override
    public String toString() {
      throw new UnsupportedOperationException("bad property doesn't have a to string");
    }

  }
}