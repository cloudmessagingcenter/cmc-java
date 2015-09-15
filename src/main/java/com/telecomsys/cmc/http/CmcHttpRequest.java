package com.telecomsys.cmc.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that holds the attributes required for all CMC requests.
 */
public class CmcHttpRequest {

    /**
     * The Request path.
     */
    private String path;

    /**
     * Request Body Parameters.
     */
    private Map<String, Object> bodyParameters;

    /**
     * Request URL Parameters.
     */
    private Map<String, String> urlParameters;

    /**
     * Timeout for the request.
     */
    private int timeout;

    /**
     * The wrapper name for the message. Default is empty, as it will disable root name wrapping.
     */
    private String messageWrapperName = "";

    /**
     * Constructor.
     *
     * @param path Request path.
     */
    public CmcHttpRequest(String path) {
        this.path = path;
        this.bodyParameters = new HashMap<String, Object>();
        this.urlParameters = new HashMap<String, String>();
    }

    /**
     * Constructor.
     *
     * @param path Request path.
     * @param bodyParameters Request Body Parameters.
     * @param timeout timeout for the request.
     */
    public CmcHttpRequest(String path, Map<String, Object> bodyParameters, int timeout) {
        this.path = path;
        this.bodyParameters = bodyParameters;
        this.timeout = timeout;
    }

    /**
     * Add parameter to the CMC request bodyParameters map.
     *
     * @param key Key for the CMC request parameter.
     * @param value Value of the CMC request parameter.
     * @return CMC request.
     */
    public CmcHttpRequest addBodyParameter(String key, Object value) {
        this.bodyParameters.put(key, value);
        return this;
    }

    /**
     * Add parameter to the CMC request urlParameters map.
     *
     * @param key Key for the CMC request parameter.
     * @param value Value of the CMC request parameter.
     * @return CMC request.
     */
    public CmcHttpRequest addUrlParameter(String key, String value) {
        this.urlParameters.put(key, value);
        return this;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the bodyParameters
     */
    public Map<String, Object> getBodyParameters() {
        return bodyParameters;
    }

    /**
     * @param bodyParameters the Body Parameters to set
     */
    public void setBodyParameters(Map<String, Object> bodyParameters) {
        this.bodyParameters = bodyParameters;
    }

    /**
     * @return the urlParameters
     */
    public Map<String, String> getUrlParameters() {
        return urlParameters;
    }

    /**
     * @param urlParameters the urlParameters to set
     */
    public void setUrlParameters(Map<String, String> urlParameters) {
        this.urlParameters = urlParameters;
    }

    /**
     * @return the timeout
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * @return the messageWrapperName
     */
    public String getMessageWrapperName() {
        return messageWrapperName;
    }

    /**
     * @param messageWrapperName the messageWrapperName to set
     */
    public void setMessageWrapperName(String messageWrapperName) {
        this.messageWrapperName = messageWrapperName;
    }

}
