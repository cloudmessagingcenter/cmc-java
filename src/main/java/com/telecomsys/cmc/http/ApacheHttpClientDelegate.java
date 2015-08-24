package com.telecomsys.cmc.http;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpMessage;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telecomsys.cmc.exception.CMCException;

/**
 * Implementation of the HTTP client request provider based on Apache HTTP components.
 */
public class ApacheHttpClientDelegate implements HttpClientDelegate {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApacheHttpClientDelegate.class);

    /**
     * The base URL to connect to the CMC REST service.
     */
    private String baseUri;

    /**
     * Apache implementation of a closeable HTTP client.
     */
    private CloseableHttpClient httpClient;

    /**
     * Json mapper.
     */
    private ObjectMapper jsonMapper;

    /**
     * Constructor.
     *
     * @param baseUri base URL to connect to the CMC REST service
     * @param httpClient HTTP client class that implements closeable interface.
     */
    public ApacheHttpClientDelegate(String baseUri, CloseableHttpClient httpClient) {
        this.baseUri = baseUri;
        this.httpClient = httpClient;
        this.jsonMapper = new ObjectMapper();
    }

    @Override
    public <T> HttpResponseWrapper<T> doGet(CmcHttpRequest request, Class<T> responseClass) throws CMCException {
        return handleRequest(request, responseClass, HttpGet.METHOD_NAME);
    }

    @Override
    public <T> HttpResponseWrapper<T> doPost(CmcHttpRequest request, Class<T> responseClass) throws CMCException {
        return handleRequest(request, responseClass, HttpPost.METHOD_NAME);
    }

    @Override
    public <T> HttpResponseWrapper<T> doPut(CmcHttpRequest request, Class<T> responseClass) throws CMCException {
        return handleRequest(request, responseClass, HttpPut.METHOD_NAME);
    }

    @Override
    public <T> HttpResponseWrapper<T> doDelete(CmcHttpRequest request, Class<T> responseClass) throws CMCException {
        return handleRequest(request, responseClass, HttpDelete.METHOD_NAME);
    }

    /**
     * Helper method to handle the request for all HTTP requests.
     *
     * @param request CMC request.
     * @param responseClass response class for HTTP response.
     * @param methodType HTTP method (i.e. POST, GET, PUT or DELETE) for now.
     * @param <T> This describes the type of response.
     * @return HTTP response wrapper
     * @throws CMCException CMC exception.
     */
    private <T> HttpResponseWrapper<T> handleRequest(CmcHttpRequest request, Class<T> responseClass, String methodType)
            throws CMCException {

        try {
            // Create the list of parameters for the request.
            List<NameValuePair> parameters = convertToNamedValuePairs(request.getParameters());

            // Generate the HTTP request based on the parameters.
            HttpRequestBase httpMethod = null;

            switch (methodType) {
            case HttpGet.METHOD_NAME:
                URI uriGet = new URIBuilder(baseUri).setPath(request.getPath()).addParameters(parameters).build();
                httpMethod = new HttpGet(uriGet);
                break;
            case HttpPost.METHOD_NAME:
                HttpPost post = new HttpPost(baseUri + request.getPath());
                HttpEntity postEntity = new UrlEncodedFormEntity(parameters, "UTF-8");
                post.setEntity(postEntity);
                httpMethod = post;
                break;
            case HttpPut.METHOD_NAME:
                HttpPut put = new HttpPut(baseUri + request.getPath());
                HttpEntity putEntity = new UrlEncodedFormEntity(parameters, "UTF-8");
                put.setEntity(putEntity);
                httpMethod = put;
                break;
            case HttpDelete.METHOD_NAME:
                URI uriDelete = new URIBuilder(baseUri).setPath(request.getPath()).addParameters(parameters).build();
                httpMethod = new HttpDelete(uriDelete);
                break;
            default:
                break;
            }

            // Add request configuration parameters if set.
            if (request.getTimeout() > 0) {
                int timeout = request.getTimeout();
                RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout).setSocketTimeout(timeout)
                        .setConnectionRequestTimeout(timeout).build();
                httpMethod.setConfig(config);
            }

            // Add the common headers required by all CMC requests
            addCMCHeaders(httpMethod);

            // Execute the request and handle the response.
            CloseableHttpResponse response = httpClient.execute(httpMethod);
            return handleResponse(httpMethod, response, responseClass);

        } catch (Exception ex) {
            throw new CMCException(ex);
        }
    }

    /**
     * Helper method to handle the responses for all HTTP requests.
     *
     * @param request CMC request.
     * @param response the HTTP response
     * @param responseClass response class for HTTP response.
     * @param <T> This describes the type of response.
     * @return HTTP response wrapper
     * @throws CMCException CMC exception.
     */
    private <T> HttpResponseWrapper<T> handleResponse(HttpRequestBase request, CloseableHttpResponse response,
            Class<T> responseClass) throws CMCException {

        HttpEntity responseEntity = response.getEntity();
        try {
            int statusCode = response.getStatusLine().getStatusCode();
            JsonNode jsonBody = jsonMapper.readTree(responseEntity.getContent());
            EntityUtils.consume(responseEntity);

            // Parse based on the HTTP response.
            if (HttpStatus.SC_OK == statusCode) {
                T responseBody = jsonMapper.convertValue(jsonBody, responseClass);
                return HttpResponseWrapper.create(statusCode, responseBody);
            } else if (statusCode >= HttpStatus.SC_BAD_REQUEST) {
                T responseBody = jsonMapper.convertValue(jsonBody, responseClass);
                return HttpResponseWrapper.create(statusCode, responseBody);
            } else {
                throw new CMCException("Invalid CMC response");
            }

        } catch (Exception ex) {
            throw new CMCException("Exception processing response: " + ex);
        } finally {
            try {
                response.close();
            } catch (IOException ioex) {
                LOGGER.error("IO Exception: {}", ioex);

            }
        }
    }

    /**
     * Helper method to set the HTTP headers required for all HTTP requests.
     *
     * @param httpMessage HTTP message
     */
    private void addCMCHeaders(HttpMessage httpMessage) {
        // Set the Accepts and Content Type headers
        httpMessage.addHeader(HttpHeaders.ACCEPT, "application/json");
        httpMessage.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        // Custom header for CSRF protection
        httpMessage.addHeader("X-Requested-By", "12345");
    }

    /**
     * Helper method to convert the map to a list of named value pairs.
     *
     * @param params map of parameters.
     * @return List of named value pairs of the parameters.
     */
    private static List<NameValuePair> convertToNamedValuePairs(Map<String, String> params) {
        List<NameValuePair> namedValueParameters = new ArrayList<NameValuePair>();
        for (Entry<String, String> entry : params.entrySet()) {
            namedValueParameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return namedValueParameters;
    }

}
