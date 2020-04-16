/*
 * Copyright 2019-2020 OpenFeign Contributors
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

package feign.http.client;

import feign.Client;
import feign.Header;
import feign.Request;
import feign.RequestOptions;
import feign.Response;
import feign.exception.FeignException;
import feign.http.HttpException;
import feign.http.HttpHeader;
import feign.http.HttpRequest;
import feign.http.HttpResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Client implementation that uses a {@link java.net.URLConnection}.
 */
public class UrlConnectionClient implements Client {

  /**
   * Send the HttpRequest to the destination. and process the response.
   *
   * @param request to send.
   * @return Response received.
   * @throws FeignException if any error occurred during processing.
   */
  @Override
  public Response request(Request request) throws FeignException {
    if (!HttpRequest.class.isAssignableFrom(request.getClass())) {
      throw new IllegalArgumentException("UrlConnectionClient only support HttpRequests");
    }
    HttpURLConnection connection = this.send((HttpRequest) request);
    return this.receive((HttpRequest) request, connection);
  }

  /**
   * Send the HttpRequest.
   *
   * @param request to send.
   * @return connection with the result of the request.
   */
  private HttpURLConnection send(HttpRequest request) {
    try {
      /* convert the uri to a url */
      URL url = request.uri().toURL();

      /* create a new connection */
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod(request.method().name());

      /* set the connection options */
      RequestOptions options = request.options();
      connection.setInstanceFollowRedirects(options.isFollowRedirects());
      connection.setConnectTimeout(options.getConnectTimeout());
      connection.setReadTimeout(options.getReadTimeout());
      connection.setAllowUserInteraction(false);

      /* apply the request headers */
      for (Header header : request.headers()) {
        header.values().forEach(value -> connection.addRequestProperty(header.name(), value));
      }

      /* write the request  */
      byte[] data = request.content();
      if (data != null) {
        connection.setDoOutput(true);
        try (OutputStream os = connection.getOutputStream()) {
          os.write(data);
        }
      }
      return connection;
    } catch (MalformedURLException mfe) {
      throw new IllegalArgumentException("Error occurred preparing the request.  The URL provided "
          + request.uri() + " is invalid", mfe);
    } catch (ProtocolException pe) {
      throw new IllegalArgumentException("Error occurred preparing the request.  The method "
          + "provided: " + request.method() + " is invalid", pe);
    } catch (IOException ioe) {
      throw new HttpException("Error occurred sending the request", ioe, request);
    }
  }

  /**
   * Receive the Response.
   *
   * @param connection with the Response.
   * @return the Response model.
   */
  private Response receive(HttpRequest request, HttpURLConnection connection) {
    try {
      /* execute the request */
      int statusCode = connection.getResponseCode();

      /* build up the common response objects */
      HttpResponse.Builder builder = HttpResponse.builder();
      builder.status(statusCode)
          .reason(connection.getResponseMessage())
          .contentLength(connection.getContentLength());

      /* copy the headers */
      connection.getHeaderFields().entrySet().stream()
          .filter(entry -> entry.getKey() != null)
          .forEach(entry -> builder.addHeader(new HttpHeader(entry.getKey(), entry.getValue())));

      boolean successful = true;
      if (statusCode > 399) {
        builder.body(connection.getErrorStream());
        successful = false;
      } else {
        builder.body(connection.getInputStream());
      }
      HttpResponse response = (HttpResponse) builder.build();
      if (!successful) {
        /* the request was not successful, read the entire response and throw */
        response.read();
        throw new HttpException("Error occurred processing the request", request, response);
      }
      /* return the response for handling */
      return response;
    } catch (IOException ioe) {
      throw new HttpException("Error occurred processing the response", ioe, request);
    }
  }
}
