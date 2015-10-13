package com.telecomsys.cmc.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpMessage;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.telecomsys.cmc.exception.CMCAuthenticationException;
import com.telecomsys.cmc.exception.CMCClientException;
import com.telecomsys.cmc.exception.CMCException;
import com.telecomsys.cmc.exception.CMCIOException;
import com.telecomsys.cmc.response.RestResponse;

/**
 * Implementation of the HTTP client request provider based on Apache HTTP components.
 */
public class ApacheHttpClientDelegate implements HttpClientDelegate {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApacheHttpClientDelegate.class);

    /**
     * CMC Version.
     */
    private static final String CMC_VERSION;

    /**
     * The base URL to connect to the CMC REST service.
     */
    private String baseUri;

    /**
     * The REST accountID used like a user name to authenticate.
     */
    private String accountID;

    /**
     * REST authentication token used like a password to authenticate.
     */
    private String authenticationToken;

    /**
     * Apache implementation of a pooling HTTP client connection manager.
     */
    private PoolingHttpClientConnectionManager connectionManager;

    /**
     * Apache HTTP client instance which is thread safe.
     */
    private CloseableHttpClient httpClient;

    /**
     * Json mapper.
     */
    private ObjectMapper jsonMapper;

    /**
     * Static initializer.
     */
    static {
        String cmcVersion = ApacheHttpClientDelegate.class.getPackage().getImplementationVersion();
        if (cmcVersion == null) {
            if (cmcVersion == null) {
                cmcVersion = ApacheHttpClientDelegate.class.getPackage().getSpecificationVersion();
                if (cmcVersion == null) {
                    cmcVersion = "CMC-development";
                }
            }
        }
        CMC_VERSION = cmcVersion;
    }

    /**
     * Constructor.
     *
     * @param baseUri base URL to connect to the CMC REST service
     * @param accountID the REST account identity.
     * @param authenticationToken the authentication token.
     */
    public ApacheHttpClientDelegate(String baseUri, String accountID, String authenticationToken) {
        this.baseUri = baseUri;
        this.accountID = accountID;
        this.authenticationToken = authenticationToken;
        this.jsonMapper = new ObjectMapper();

        // Support root name parsing by jackson.
        this.jsonMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        this.jsonMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);

        // Do not serialize properties that are null.
        this.jsonMapper.setSerializationInclusion(Include.NON_NULL);

        // Usage a connection manager that pools connections.
        this.connectionManager = new PoolingHttpClientConnectionManager();

        // Set defaults for the connections. The default per route is 2 and max total is 20. Increasing the limits.
        this.connectionManager.setMaxTotal(20);
        this.connectionManager.setDefaultMaxPerRoute(10);
        this.httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();
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
            // Generate the HTTP request based on the parameters.
            HttpRequestBase httpMethod = null;

            switch (methodType) {
            case HttpGet.METHOD_NAME:
                // Create the list of parameters for the request.
                List<NameValuePair> getParams = convertToNamedValuePairs(request.getUrlParameters());
                URI uriGet = null;
                if (getParams.size() > 0) {
                    uriGet = new URIBuilder(baseUri).setPath(request.getPath()).addParameters(getParams).build();
                } else {
                    uriGet = new URIBuilder(baseUri).setPath(request.getPath()).build();
                }
                httpMethod = new HttpGet(uriGet);
                break;
            case HttpPost.METHOD_NAME:
                HttpPost post = new HttpPost(baseUri + request.getPath());
                ObjectWriter postWriter = jsonMapper.writer().withRootName(request.getMessageWrapperName());
                String jsonPostParams = postWriter.writeValueAsString(request.getBodyParameters());
                StringEntity postEntity = new StringEntity(jsonPostParams, ContentType.APPLICATION_JSON);
                post.setEntity(postEntity);
                httpMethod = post;
                break;
            case HttpPut.METHOD_NAME:
                HttpPut put = new HttpPut(baseUri + request.getPath());
                ObjectWriter putWriter = jsonMapper.writer().withRootName(request.getMessageWrapperName());
                String jsonPutParams = putWriter.writeValueAsString(request.getBodyParameters());
                StringEntity putEntity = new StringEntity(jsonPutParams, ContentType.APPLICATION_JSON);
                put.setEntity(putEntity);
                httpMethod = put;
                break;
            case HttpDelete.METHOD_NAME:
                // Create the list of parameters for the request.
                List<NameValuePair> deleteParams = convertToNamedValuePairs(request.getUrlParameters());
                URI uriDelete = null;
                if (deleteParams.size() > 0) {
                    uriDelete = new URIBuilder(baseUri).setPath(request.getPath()).addParameters(deleteParams).build();
                } else {
                    uriDelete = new URIBuilder(baseUri).setPath(request.getPath()).build();
                }
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

        } catch (CMCException cmcex) {
            throw cmcex;
        } catch (IOException ioex) {
            throw new CMCIOException(ioex);
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
        if (responseEntity == null) {
            throw new CMCException("Response is empty");
        }

        try {
            int statusCode = response.getStatusLine().getStatusCode();
            InputStream jsonStream = responseEntity.getContent();

            // Parse based on the HTTP response.
            if (HttpStatus.SC_OK == statusCode || statusCode == HttpStatus.SC_NOT_FOUND) {
                T responseBody = jsonMapper.readValue(jsonStream, responseClass);
                return HttpResponseWrapper.create(statusCode, responseBody);
            } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                throw new CMCAuthenticationException("Authentication failed");
            } else if (statusCode >= HttpStatus.SC_BAD_REQUEST && statusCode < HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                // These are the http status code specifically related to client error.
                RestResponse error = jsonMapper.readValue(jsonStream, RestResponse.class);
                throw new CMCClientException(error, statusCode);
            } else {
                throw new CMCException("Invalid CMC response");
            }

        } catch (IllegalStateException | IOException iex) {
            throw new CMCException("Exception processing response: " + iex);
        } finally {
            try {
                EntityUtils.consume(responseEntity);
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

        // Add a user agent header
        httpMessage.addHeader("User-Agent", "cmc-java " + CMC_VERSION);

        // Set the basic authorization headers.
        String auth = accountID + ":" + authenticationToken;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth, StandardCharsets.US_ASCII);
        httpMessage.addHeader(HttpHeaders.AUTHORIZATION, authHeader);
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
