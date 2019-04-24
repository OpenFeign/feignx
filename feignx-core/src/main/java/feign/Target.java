package feign;

public interface Target<T> {

  Class<T> type();

  String name();

}
