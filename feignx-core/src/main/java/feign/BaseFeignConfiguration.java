package feign;

import feign.http.Client;
import feign.http.RequestEncoder;
import feign.http.ResponseDecoder;

public class BaseFeignConfiguration implements FeignConfiguration {

  private final Client client;
  private final RequestEncoder requestEncoder;
  private final ResponseDecoder responseDecoder;
  private final Contract contract;
  private final Target target;

  public BaseFeignConfiguration(Client client, RequestEncoder requestEncoder,
      ResponseDecoder responseDecoder, Contract contract, Target target) {
    this.client = client;
    this.requestEncoder = requestEncoder;
    this.responseDecoder = responseDecoder;
    this.contract = contract;
    this.target = target;
  }

  @Override
  public Client getClient() {
    return this.client;
  }

  @Override
  public RequestEncoder getRequestEncoder() {
    return this.requestEncoder;
  }

  @Override
  public ResponseDecoder getResponseDecoder() {
    return this.responseDecoder;
  }

  @Override
  public Contract getContract() {
    return this.contract;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Target<T> getTarget() {
    return this.target;
  }
}
