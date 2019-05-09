package feign;

import feign.exception.FeignException;
import feign.http.HttpException;

/**
 * Target Client instance responsible for submitting the Request to the Target and processing
 * the Response.
 */
public interface Client {

  /**
   * Submit the Request to the Target.
   *
   * @param request to be sent.
   * @return a Response for the Request.
   * @throws FeignException if any errors occur.
   */
  Response request(Request request) throws FeignException;
}
