package feign.impl;

import feign.exception.ExceptionHandler;
import feign.http.Client;
import feign.http.RequestEncoder;
import feign.http.RequestInterceptor;
import feign.http.Response;
import feign.http.ResponseDecoder;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.RunnableFuture;

public class BlockingTargetMethodHandler extends AbstractTargetMethodHandler {

  protected BlockingTargetMethodHandler(TargetMethodMetadata targetMethodMetadata,
      RequestEncoder encoder, List<RequestInterceptor> interceptors,
      Client client, ResponseDecoder decoder,
      ExceptionHandler exceptionHandler, Executor executor) {
    super(targetMethodMetadata, encoder, interceptors, client, decoder, exceptionHandler, executor);
  }

  @Override
  protected Object handleTask(RunnableFuture<Response> task) {
    try {
      /* pull the result of the task immediately, waiting for it to complete */
      Response response = task.get();

      /* decode the response */
      return this.decode(response);
    } catch (InterruptedException | ExecutionException ex) {
      /* dispatch to the error handler */
      this.getExceptionHandler().accept(ex);
    }
    return this.methodNotHandled();
  }
}
