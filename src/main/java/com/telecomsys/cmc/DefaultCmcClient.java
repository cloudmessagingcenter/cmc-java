package com.telecomsys.cmc;

import com.telecomsys.cmc.exception.CMCException;
import com.telecomsys.cmc.http.ApacheHttpClientDelegate;
import com.telecomsys.cmc.http.CmcHttpRequest;
import com.telecomsys.cmc.http.HttpClientDelegate;

/**
 * Default implementation of the interface used for communication with the CMC REST service.
 */
public class DefaultCmcClient implements CmcClient {

    /**
     * HTTP client instance.
     */
    private HttpClientDelegate httpClient;

    /**
     * Constructor.
     *
     * @param baseUri base URL to connect to the CMC REST service.
     * @param accountID the REST account identity.
     * @param authenticationToken the authentication token.
     */
    public DefaultCmcClient(String baseUri, String accountID, String authenticationToken) {
        httpClient = new ApacheHttpClientDelegate(baseUri, accountID, authenticationToken);
    }

    @Override
    public void sendMessage(String destinations, String keyword, String message) throws CMCException {

        // Create the request with parameters.
        CmcHttpRequest cmcRequest = new CmcHttpRequest("messages");
        cmcRequest.addParameter("to", destinations);
        cmcRequest.addParameter("from", keyword);
        cmcRequest.addParameter("message", message);
        cmcRequest.setMessageWrapperName("sendmessage");

        // Send the message to CMC.
        httpClient.doPost(cmcRequest, String.class);
    }

}
