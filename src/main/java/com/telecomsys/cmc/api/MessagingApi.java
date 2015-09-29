package com.telecomsys.cmc.api;

import com.telecomsys.cmc.exception.CMCException;
import com.telecomsys.cmc.http.CmcHttpRequest;
import com.telecomsys.cmc.http.HttpResponseWrapper;
import com.telecomsys.cmc.model.Message;
import com.telecomsys.cmc.response.NotificationsResponse;

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
     * @param message the Message model that has all message details.
     * @return HttpResponseWrapper http response wrapper with the response.
     * @throws CMCException CMC exception if errors.
     */
    public HttpResponseWrapper<NotificationsResponse> sendMessage(Message message) throws CMCException {

        // Create the request with parameters.
        CmcHttpRequest cmcRequest = new CmcHttpRequest(MESSAGING_URL);
        cmcRequest.addBodyParameter("to", message.getDestinations());
        cmcRequest.addBodyParameter("from", message.getKeyword());
        cmcRequest.addBodyParameter("message", message.getMessage());

        String subject = message.getSubject();
        if (subject != null && subject.length() > 0) {
            cmcRequest.addBodyParameter("subject", subject);
        }
        String notifyURL = message.getNotifyURL();
        if (notifyURL != null && notifyURL.length() > 0) {
            cmcRequest.addBodyParameter("notifyURL", notifyURL);
        }
        Integer replyexpiry = message.getReplyExpiry();
        if (replyexpiry != null) {
            cmcRequest.addBodyParameter("replyexpiry", replyexpiry);
        }
        cmcRequest.setMessageWrapperName("sendmessage");

        // Send the message to CMC.
        return httpClient.doPost(cmcRequest, NotificationsResponse.class);
    }

}
