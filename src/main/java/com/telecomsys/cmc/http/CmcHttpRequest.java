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
     * Request parameters.
     */
    private Map<String, String> parameters;
    
    /**
     * Timeout for the request.
     */
    private Integer timeout;
    
    /**
     * Constructor.
     * 
     * @param path Request path.
     */
    public CmcHttpRequest(String path) {
        this.path = path;
        this.parameters = new HashMap<String, String>();
    }

    /**
     * Constructor.
     * 
     * @param path Request path.
     * @param parameters Request parameters.
     * @param timeout timeout for the request.
     */    
    public CmcHttpRequest(String path, Map<String, String> parameters, Integer timeout) {
        this.path = path;
        this.parameters = parameters;
        this.timeout = timeout;
    }
      
    public CmcHttpRequest addParameter(String key, String value) {
        this.parameters.put(key, value);
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
     * @return the parameters
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    /**
     * @return the timeout
     */
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
   
}