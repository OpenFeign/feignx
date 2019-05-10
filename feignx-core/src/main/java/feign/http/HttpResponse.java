package feign.http;

import feign.Header;
import feign.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Http Response model.
 */
public final class HttpResponse implements Response {

  private int status;
  private String reason;
  private List<HttpHeader> headers = new ArrayList<>();
  private InputStream body;
  private int contentLength;

  /**
   * Creates a new HttpResponse Builder.
   *
   * @return a new builder instance.
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Creates a new HttpResponse.
   */
  HttpResponse() {
    super();
  }

  /**
   * The Response Status Code.
   *
   * @param status code.
   */
  private void status(int status) {
    this.status = status;
  }

  /**
   * Http Status Code.
   *
   * @return status code.
   */
  public int status() {
    return this.status;
  }

  /**
   * Set the reason from the Response.
   *
   * @param reason response reason.
   */
  private void reason(String reason) {
    this.reason = reason;
  }

  /**
   * HTTP Status Reason.
   *
   * @return status reason.
   */
  public String reason() {
    return this.reason;
  }

  /**
   * Set the Content Length.
   *
   * @param contentLength of the response.
   */
  private void contentLength(int contentLength) {
    this.contentLength = contentLength;
  }

  /**
   * The length of the Response.
   *
   * @return content length.
   */
  public int contentLength() {
    return this.contentLength;
  }

  /**
   * Set the Response body.
   *
   * @param body input stream.
   */
  private void body(InputStream body) {
    this.body = body;
  }

  /**
   * An Input Stream backed by the Response data.  It is the responsibility of the caller
   * to close this stream.
   *
   * @return a response data backed Input Stream.
   */
  public InputStream body() {
    return this.body;
  }

  /**
   * List of Headers provided in the Response.
   *
   * @return response headers.
   */
  public List<Header> headers() {
    return Collections.unmodifiableList(this.headers);
  }

  /**
   * Add the Header to the Response.
   *
   * @param header to add.
   */
  private void addHeader(HttpHeader header) {
    this.headers.add(header);
  }

  /**
   * Reads the entire response and returns the data as a byte array.
   *
   * @return the Response data as a byte array.
   * @throws IOException if the response could not be read.
   */
  public byte[] toByteArray() throws IOException {
    try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
      byte[] input = new byte[this.contentLength];
      int len;
      while ((len = this.body.read(input, 0, input.length)) != -1) {
        buffer.write(input, 0, len);
      }
      buffer.flush();
      return buffer.toByteArray();
    }
  }

  /**
   * Close the Response.
   *
   * @throws Exception if the response cannot be closed.
   */
  @Override
  public void close() throws Exception {
    /* close our input stream if exists is not already closed */
    if (this.body != null) {
      this.body.close();
    }
  }

  public static class Builder {

    private HttpResponse response = new HttpResponse();

    public Builder status(int status) {
      response.status(status);
      return this;
    }

    public Builder reason(String reason) {
      response.reason(reason);
      return this;
    }

    public Builder contentLength(int contentLength) {
      response.contentLength(contentLength);
      return this;
    }

    public Builder addHeader(HttpHeader header) {
      response.addHeader(header);
      return this;
    }

    public Builder body(InputStream body) {
      response.body(body);
      return this;
    }

    public Response build() {
      return response;
    }

  }
}
