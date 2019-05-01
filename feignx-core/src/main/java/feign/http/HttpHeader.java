package feign.http;

import feign.support.Assert;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class HttpHeader {

  private final String name;
  private final Set<String> values = new LinkedHashSet<>();

  public HttpHeader(String name) {
    Assert.isNotEmpty(name, "name is required.");
    this.name = name;
  }

  public HttpHeader(String name, Collection<String> values) {
    this(name);
    Assert.isNotNull(values, "values cannot be null.");

    /* create a deep copy of the values */
    this.values.addAll(values);
  }

  public void value(String value) {
    Assert.isNotEmpty(value, "value is required.");
    this.values.add(value);
  }

  public void clear() {
    this.values.clear();
  }

  public Collection<String> values() {
    return Collections.unmodifiableSet(this.values);
  }

  public String name() {
    return this.name;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof HttpHeader)) {
      return false;
    }
    HttpHeader that = (HttpHeader) obj;
    return name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return "HttpHeader: [" + "name='" + name + "'" + ", values=" + values + "]";
  }
}
