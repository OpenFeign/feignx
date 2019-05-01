package feign.impl;

import feign.Client;
import feign.Contract;
import feign.FeignConfiguration;
import feign.RequestEncoder;
import feign.RequestInterceptor;
import feign.ResponseDecoder;
import feign.Target;
import feign.exception.ExceptionHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public class BaseFeignConfiguration implements FeignConfiguration {

  private final Client client;
  private final RequestEncoder requestEncoder;
  private final ResponseDecoder responseDecoder;
  private final Contract contract;
  private final Target target;
  private final Executor executor;
  private final List<RequestInterceptor> interceptors = new ArrayList<>();
  private final ExceptionHandler exceptionHandler;

  public BaseFeignConfiguration(Target target, Contract contract, RequestEncoder encoder,
      List<RequestInterceptor> interceptors, Client client, ResponseDecoder decoder,
      ExceptionHandler exceptionHandler, Executor executor) {
    this.client = client;
    this.requestEncoder = encoder;
    this.responseDecoder = decoder;
    this.contract = contract;
    this.executor = executor;
    this.target = target;
    this.interceptors.addAll(interceptors);
    this.exceptionHandler = exceptionHandler;
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

  @Override
  public Executor getExecutor() {
    return this.executor;
  }

  @Override
  public List<RequestInterceptor> getRequestInterceptors() {
    return Collections.unmodifiableList(this.interceptors);
  }

  @Override
  public ExceptionHandler getExceptionHandler() {
    return this.exceptionHandler;
  }
}
