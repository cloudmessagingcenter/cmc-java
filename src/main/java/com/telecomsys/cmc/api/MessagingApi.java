package com.telecomsys.cmc.api;

import com.telecomsys.cmc.exception.CMCException;
import com.telecomsys.cmc.http.CmcHttpRequest;

/**
 * Main facade for messaging using the CMC REST API.
 */
public class MessagingApi extends CmcBaseApi {

    /**
     * Messaging End point.
     */
    public static final String MESSAGING_URL = "/messages";

    /**
     * Constructor.
     *
     * @param baseUri base URL to connect to the CMC REST service.
     * @param accountID the REST account identity.
     * @param authenticationToken the authentication token.
     */
    public MessagingApi(String baseUri, String accountID, String authenticationToken) {
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
        CmcHttpRequest cmcRequest = new CmcHttpRequest(MESSAGING_URL);
        cmcRequest.addBodyParameter("to", destinations);
        cmcRequest.addBodyParameter("from", keyword);
        cmcRequest.addBodyParameter("message", message);
        cmcRequest.setMessageWrapperName("sendmessage");

        // Send the message to CMC.
        httpClient.doPost(cmcRequest, String.class);
    }

}
