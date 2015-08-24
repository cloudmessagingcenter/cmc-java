package com.telecomsys.cmc.http;

/**
 * Class that acts as a wrapper to the response and HTTP status code.
 *
 * @param <T> This describes the type of response.
 */
public class HttpResponseWrapper<T> {

    /**
     * HTTP status code.
     */
    private int httpStatusCode;

    /**
     * HTTP response.
     */
    private T responseBody;

    /**
     * Create the response wrapper based on the status code and the response body.
     *
     * @param httpStatusCode HTTP status code.
     * @param responseBody HTTP response body.
     * @param <T> This describes the type of response.
     * @return HTTP response wrapper.
     */
    public static <T> HttpResponseWrapper<T> create(int httpStatusCode, T responseBody) {
        HttpResponseWrapper<T> wrapper = new HttpResponseWrapper<T>();

        wrapper.setHttpStatusCode(httpStatusCode);
        wrapper.setResponseBody(responseBody);

        return wrapper;
    }

    /**
     * @return the httpStatusCode
     */
    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    /**
     * @param httpStatusCode the httpStatusCode to set
     */
    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    /**
     * @return the responseBody
     */
    public T getResponseBody() {
        return responseBody;
    }

    /**
     * @param responseBody the responseBody to set
     */
    public void setResponseBody(T responseBody) {
        this.responseBody = responseBody;
    }

}
