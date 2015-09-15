package com.telecomsys.cmc.api;

import com.telecomsys.cmc.http.ApacheHttpClientDelegate;
import com.telecomsys.cmc.http.HttpClientDelegate;

/**
 * Base abstract class for all API facades.
 */
public abstract class CmcBaseApi {

    /**
     * HTTP client instance.
     */
    protected HttpClientDelegate httpClient;

    /**
     * Constructor.
     *
     * @param baseUri base URL to connect to the CMC REST service.
     * @param accountID the REST account identity.
     * @param authenticationToken the authentication token.
     */
    public CmcBaseApi(String baseUri, String accountID, String authenticationToken) {
        httpClient = new ApacheHttpClientDelegate(baseUri, accountID, authenticationToken);
    }

}
