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

package feign.impl;

import feign.Client;
import feign.Contract;
import feign.ExceptionHandler;
import feign.Feign.FeignConfigurationBuilderImpl;
import feign.FeignConfiguration;
import feign.Logger;
import feign.RequestEncoder;
import feign.RequestInterceptor;
import feign.ResponseDecoder;
import feign.Retry;
import feign.http.RequestSpecification;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * Starting point for all Feign Configuration instances.
 */
public class BaseFeignConfiguration implements FeignConfiguration {

  private final Consumer<RequestSpecification> target;
  private final Client client;
  private final RequestEncoder requestEncoder;
  private final ResponseDecoder responseDecoder;
  private final Contract contract;
  private final Executor executor;
  private final List<RequestInterceptor> interceptors = new ArrayList<>();
  private final ExceptionHandler exceptionHandler;
  private final Logger logger;
  private final Retry retry;

  /**
   * Creates a new Base Feign Configuration.
   *
   * @param builder containing the configuration to use.
   */
  public BaseFeignConfiguration(FeignConfigurationBuilderImpl builder) {
    this.target = builder.target;
    this.client = builder.client;
    this.requestEncoder = builder.encoder;
    this.responseDecoder = builder.decoder;
    this.contract = builder.contract;
    this.executor = builder.executor;
    this.interceptors.addAll(builder.interceptors);
    this.exceptionHandler = builder.exceptionHandler;
    this.logger = builder.logger;
    this.retry = builder.retry;
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

  @Override
  public Logger getLogger() {
    return this.logger;
  }

  @Override
  public Retry getRetry() {
    return this.retry;
  }

  @Override
  public Consumer<RequestSpecification> getTarget() {
    return this.target;
  }
}
