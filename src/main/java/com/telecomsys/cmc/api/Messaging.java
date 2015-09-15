package com.telecomsys.cmc.api;

import com.telecomsys.cmc.exception.CMCException;
import com.telecomsys.cmc.http.CmcHttpRequest;

/**
 * Main facade for messaging using the CMC REST API.
 */
public class Messaging extends CmcBaseApi {

    /**
     * Constructor.
     *
     * @param baseUri base URL to connect to the CMC REST service.
     * @param accountID the REST account identity.
     * @param authenticationToken the authentication token.
     */
    public Messaging(String baseUri, String accountID, String authenticationToken) {
        super(baseUri, accountID, authenticationToken);
    }

    /**
     * Method to send a message using CMC REST API.
     *
     * @param destinations the MIN or groups to send the message to.
     * @param keyword the keyword used to identify the REST connection.
     * @param message the message to be sent.
     * @throws CMCException CMC exception if errors.
     */
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
