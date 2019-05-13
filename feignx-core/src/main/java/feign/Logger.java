package feign;

/**
 * Logger component responsible for log messages pertaining to specific components involved
 * in the Request/Response process.
 */
public interface Logger {

  /**
   * Log the {@link Request}.
   *
   * @param methodName of the method making the request.
   * @param request to log.
   */
  void logRequest(String methodName, Request request);

  /**
   * Log the {@link Response}.
   *
   * @param methodName of the method making the request.
   * @param response to log.
   */
  void logResponse(String methodName, Response response);

}
