package feign;

public interface FeignConfiguration<S extends FeignConfiguration<S>> {

  S client();

  S encoder();

  S decoder();

}
