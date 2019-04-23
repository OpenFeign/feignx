package feign;

public interface Contract {

  void execute(Target<?> target);

}
