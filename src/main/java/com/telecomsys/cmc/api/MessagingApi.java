package com.telecomsys.cmc.api;

import java.util.List;

import com.telecomsys.cmc.exception.CMCException;
import com.telecomsys.cmc.http.CmcHttpRequest;
import com.telecomsys.cmc.http.HttpResponseWrapper;
import com.telecomsys.cmc.model.Message;
import com.telecomsys.cmc.response.DeliveryReceiptResponse;
import com.telecomsys.cmc.response.MessageRepliesResponse;
import com.telecomsys.cmc.response.NotificationsResponse;
import com.telecomsys.cmc.utils.StringUtils;

/**
 * Main facade for messaging using the CMC REST API.
 */
public class MessagingApi extends CmcBaseApi {

    /**
     * Messaging End point.
     */
    public static final String MESSAGING_URL = "/messages";

    /**
     * Delivery Notifications End point.
     */
    public static final String NOTIFICATIONS_URL = "/notifications";

    /**
     * Delivery Receipts End point.
     */
    public static final String RECEIPTS_URL = "/receipts";

    /**
     * Replies End point.
     */
    public static final String REPLIES_URL = "/replies";

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

    /**
     * Method to retrieve delivery notifications using CMC REST API.
     *
     * @param trackingID the unique job tracking ID returned during a send or schedule message.
     * @return HttpResponseWrapper http response wrapper with the response.
     * @throws CMCException CMC exception if errors.
     */
    public HttpResponseWrapper<NotificationsResponse> getDeliveryNotifications(String trackingID) throws CMCException {

        // Append the matrix parameters
        StringBuilder sb = new StringBuilder();
        sb.append(NOTIFICATIONS_URL).append("/").append(trackingID);

        // Create the request with parameters.
        CmcHttpRequest cmcRequest = new CmcHttpRequest(sb.toString());

        // Send the message to CMC.
        return httpClient.doGet(cmcRequest, NotificationsResponse.class);
    }

    /**
     * Method to retrieve delivery receipts using CMC REST API.
     *
     * @param messageIDs the message IDs for which a delivery receipt is requested.
     * @return HttpResponseWrapper http response wrapper with the response.
     * @throws CMCException CMC exception if errors.
     */
    public HttpResponseWrapper<DeliveryReceiptResponse> getDeliveryReceipts(List<String> messageIDs)
            throws CMCException {

        // Append the matrix parameters
        StringBuilder sb = new StringBuilder();
        sb.append(RECEIPTS_URL).append("/");

        String messageIDsStr = StringUtils.convertStringListToCSV(messageIDs);
        if (messageIDsStr != null) {
            sb.append(messageIDsStr);
        }

        // Create the request with parameters.
        CmcHttpRequest cmcRequest = new CmcHttpRequest(sb.toString());

        // Send the message to CMC.
        return httpClient.doGet(cmcRequest, DeliveryReceiptResponse.class);
    }

    /**
     * Method to retrieve replies using CMC REST API.
     *
     * @param messageID the message ID for which the replies is requested.
     * @return HttpResponseWrapper http response wrapper with the response.
     * @throws CMCException CMC exception if errors.
     */
    public HttpResponseWrapper<MessageRepliesResponse> getReplies(String messageID)
            throws CMCException {

        // Append the matrix parameters
        StringBuilder sb = new StringBuilder();
        sb.append(REPLIES_URL).append("/").append(messageID);

        // Create the request with parameters.
        CmcHttpRequest cmcRequest = new CmcHttpRequest(sb.toString());

        // Send the message to CMC.
        return httpClient.doGet(cmcRequest, MessageRepliesResponse.class);
    }

}
