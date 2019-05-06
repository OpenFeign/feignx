package feign.impl.type;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;

public class WildCardTypeDefinition implements WildcardType, TypeDefinition {

  private List<TypeDefinition> upperBounds;
  private List<TypeDefinition> lowerBounds;

  WildCardTypeDefinition() {
    super();
    this.upperBounds = new ArrayList<>();
    this.lowerBounds = new ArrayList<>();
  }

  @Override
  public Class<?> getType() {
    /* only one upper or lower bound is allowed, so first default to the lower bound */
    if (!this.lowerBounds.isEmpty()) {
      return this.lowerBounds.get(0).getType();
    } else if (!this.upperBounds.isEmpty()) {
      return this.upperBounds.get(0).getType();
    }
    return null;
  }

  @Override
  public Type[] getUpperBounds() {
    return this.upperBounds.toArray(new Type[]{});
  }

  @Override
  public Type[] getLowerBounds() {
    return this.lowerBounds.toArray(new Type[]{});
  }

  void addUpperBound(TypeDefinition typeDefinition) {
    this.upperBounds.add(typeDefinition);
  }

  void addLowerBound(TypeDefinition typeDefinition) {
    this.lowerBounds.add(typeDefinition);
  }
}
