package com.telecomsys.cmc.api;

import java.util.List;

import com.telecomsys.cmc.exception.CMCException;
import com.telecomsys.cmc.http.CmcHttpRequest;
import com.telecomsys.cmc.http.HttpResponseWrapper;
import com.telecomsys.cmc.model.Message;
import com.telecomsys.cmc.model.ScheduleMessage;
import com.telecomsys.cmc.response.RestResponse;
import com.telecomsys.cmc.utils.StringUtils;

/**
 * Main facade for scheduling using the CMC REST API.
 */
public class SchedulingApi extends CmcBaseApi {

    /**
     * Scheduling End point.
     */
    public static final String SCHEDULING_URL = "/schedulemessage";

    /**
     * Constructor.
     *
     * @param baseUri base URL to connect to the CMC REST service.
     * @param accountID the REST account identity.
     * @param authenticationToken the authentication token.
     */
    public SchedulingApi(String baseUri, String accountID, String authenticationToken) {
        super(baseUri, accountID, authenticationToken);
    }

    /**
     * Method to schedule a message using CMC REST API.
     *
     * @param message the Message model that has all message details.
     * @return HttpResponseWrapper http response wrapper with the response.
     * @throws CMCException CMC exception if errors.
     */
    public HttpResponseWrapper<ScheduleMessage> scheduleMessage(Message message) throws CMCException {

        // Create the request with parameters.
        CmcHttpRequest cmcRequest = new CmcHttpRequest(SCHEDULING_URL);
        cmcRequest.addBodyParameter("to", message.getDestinations());
        cmcRequest.addBodyParameter("from", message.getKeyword());
        cmcRequest.addBodyParameter("message", message.getMessage());

        String subject = message.getSubject();
        if (subject != null && subject.length() > 0) {
            cmcRequest.addBodyParameter("subject", subject);
        }
        cmcRequest.setMessageWrapperName("schedulemessage");

        // Send the message to CMC.
        return httpClient.doPost(cmcRequest, ScheduleMessage.class);
    }

    /**
     * Method to delete scheduled messages(s) using CMC REST API.
     *
     * @param messageIds List of scheduled messages to be deleted identified by their message ID.
     * @return HttpResponseWrapper http response wrapper with the response.
     * @throws CMCException CMC exception if errors.
     */
    public HttpResponseWrapper<RestResponse> deleteContacts(List<String> messageIds) throws CMCException {

        // Append the matrix parameters
        StringBuilder sb = new StringBuilder();
        sb.append(SCHEDULING_URL).append("/");
        String messageIdStr = StringUtils.convertStringListToCSV(messageIds);
        if (messageIdStr != null) {
            sb.append(messageIdStr);
        }

        // Create the request with parameters.
        CmcHttpRequest cmcRequest = new CmcHttpRequest(sb.toString());

        // Send the request to CMC.
        return httpClient.doDelete(cmcRequest, RestResponse.class);
    }

}
