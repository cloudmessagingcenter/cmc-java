package com.telecomsys.cmc;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.telecomsys.cmc.api.MessagingApi;
import com.telecomsys.cmc.exception.CMCAuthenticationException;
import com.telecomsys.cmc.exception.CMCException;
import com.telecomsys.cmc.exception.CMCIOException;
import com.telecomsys.cmc.exception.CMCClientException;
import com.telecomsys.cmc.http.HttpResponseWrapper;
import com.telecomsys.cmc.model.Message;
import com.telecomsys.cmc.model.ProgramReply;
import com.telecomsys.cmc.response.DeliveryReceipt;
import com.telecomsys.cmc.response.DeliveryReceiptResponse;
import com.telecomsys.cmc.response.MessageReplies;
import com.telecomsys.cmc.response.MessageRepliesResponse;
import com.telecomsys.cmc.response.MessageReply;
import com.telecomsys.cmc.response.MessageStatus;
import com.telecomsys.cmc.response.Notifications;
import com.telecomsys.cmc.response.NotificationsResponse;
import com.telecomsys.cmc.response.RestResponse;
import com.telecomsys.cmc.response.TrackingInformation;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

public class MessagingApiTest {
    
    /**
     * CMC REST user name (account ID).
     */
    private static final String USERNAME = "9876";

    /**
     * CMC REST user name (account ID).
     */
    private static final String PASSWORD = "1234";

    /**
     * CMC REST connection keyword.
     */
    private static final String REST_CONNECTION_KEYWORD = "scsrest"; 
    
    /**
     * CMC client instance.
     */
    private MessagingApi messagingApi;
    
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(18089);
    
    @Before
    public void setup() {
        messagingApi = new MessagingApi("http://localhost:18089", USERNAME, PASSWORD);
    }
    
    @Test(expected=CMCIOException.class)
    public void invalidHostTest() throws CMCException {
        MessagingApi invalidMessagingApi = new MessagingApi("http://invalidHost:1234", USERNAME, PASSWORD);
        List<String> destinations = new ArrayList<String>();
        destinations.add("4102804827");   
        Message message = new Message(destinations, REST_CONNECTION_KEYWORD, "Test message");
        invalidMessagingApi.sendMessage(message);        
    }
    
    @Test(expected=CMCAuthenticationException.class)
    public void invalidCredentialsTest() throws CMCException {
        stubFor(post(urlEqualTo("/messages"))
                .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader("Content-Type", "text/html")
                    .withBody("This request requires HTTP authentication.")));
        
        List<String> destinations = new ArrayList<String>();
        destinations.add("4102804827");
        Message message = new Message(destinations, REST_CONNECTION_KEYWORD, "Test message");
        messagingApi.sendMessage(message);
    }     
    
    @Test
    public void invalidKeywordTest() throws CMCException {
        stubFor(post(urlEqualTo("/messages"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"1010\",\"message\":\"Your message failed: Invalid from address.\"}}")));        
        
        try { 
            List<String> destinations = new ArrayList<String>();
            destinations.add("4102804827");
            Message message = new Message(destinations, REST_CONNECTION_KEYWORD, "Test message");
            messagingApi.sendMessage(message);
        } catch (CMCClientException cmex) {
            RestResponse error = cmex.getError();
            assertEquals(error.getStatus(), "fail");
            assertEquals(error.getCode(), "1010");
            assertEquals(error.getMessage(), "Your message failed: Invalid from address.");
        }
    }
    
    @Test
    public void sendMessagesSingleDestination() throws CMCException {
        stubFor(post(urlEqualTo("/messages"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"success\",\"notifications\":{\"to\":[\"4102804827\"],\"from\":\"scsrest\",\"trackinginformation\":[{\"destination\":\"4102804827\",\"messagestatus\":\"Message Accepted\",\"messageID\":\"GW1_AVvciGlHRM32pw0Q\",\"messagetext\":\"Test message\"}]}}}")));        
        
        List<String> destinations = new ArrayList<String>();
        destinations.add("4102804827"); 
        Message message = new Message(destinations, REST_CONNECTION_KEYWORD, "Test message");
        HttpResponseWrapper<NotificationsResponse> response = messagingApi.sendMessage(message);
        
        // Verify the response
        assertEquals(response.getHttpStatusCode(), 200);
        Notifications notifications = response.getResponseBody().getNotifications();
        assertEquals(notifications.getFromAddress(),"scsrest");
        assertEquals(notifications.getTo().size(),1);
        assertEquals(notifications.getTo().get(0),"4102804827");
        List<TrackingInformation> trackingInformation = notifications.getTrackingInformation();
        assertEquals(trackingInformation.size(),1);
        assertEquals(trackingInformation.get(0).getDestination(),"4102804827");
        assertEquals(trackingInformation.get(0).getMessageID(),"GW1_AVvciGlHRM32pw0Q");
        assertEquals(trackingInformation.get(0).getMessageText(),"Test message");
        
        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlMatching("/messages")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"sendmessage\":{\"message\":\"Test message\",\"to\":[\"4102804827\"],\"from\":\"scsrest\"}}");
    }    
    
    @Test
    public void sendMessagesMultipleDestinations() throws CMCException {
        stubFor(post(urlEqualTo("/messages"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"success\",\"notifications\":{\"to\":[\"4102804827\",\"4102804828\"],\"from\":\"scsrest\",\"trackinginformation\":[{\"destination\":\"4102804827\",\"messagestatus\":\"Message Accepted\",\"messageID\":\"GW1_AVvciGlHRM32pw0Q\",\"messagetext\":\"Test message\"},{\"destination\":\"4102804828\",\"messagestatus\":\"Message Accepted\",\"messageID\":\"GW1_AVvciGlHRM32Rg0O\",\"messagetext\":\"Test message\"}]}}}")));        
        
        List<String> destinations = new ArrayList<String>();
        destinations.add("4102804827");
        destinations.add("4102804828"); 
        Message message = new Message(destinations, REST_CONNECTION_KEYWORD, "Test message");
        message.setNotifyURL("http://customer.com/notifications");
        message.setReplyExpiry(60);
        HttpResponseWrapper<NotificationsResponse> response = messagingApi.sendMessage(message);
        
        // Verify the response
        assertEquals(response.getHttpStatusCode(), 200);
        Notifications notifications = response.getResponseBody().getNotifications();
        assertEquals(notifications.getFromAddress(),"scsrest");
        assertEquals(notifications.getTo().size(),2);
        assertEquals(notifications.getTo().get(0),"4102804827");
        assertEquals(notifications.getTo().get(1),"4102804828");
        List<TrackingInformation> trackingInformation = notifications.getTrackingInformation();
        assertEquals(trackingInformation.size(),2);
        assertEquals(trackingInformation.get(0).getDestination(),"4102804827");
        assertEquals(trackingInformation.get(0).getMessageID(),"GW1_AVvciGlHRM32pw0Q");
        assertEquals(trackingInformation.get(0).getMessageText(),"Test message");
        assertEquals(trackingInformation.get(1).getDestination(),"4102804828");
        assertEquals(trackingInformation.get(1).getMessageID(),"GW1_AVvciGlHRM32Rg0O");
        assertEquals(trackingInformation.get(1).getMessageText(),"Test message");

        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlMatching("/messages")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"sendmessage\":{\"message\":\"Test message\",\"notifyURL\":\"http://customer.com/notifications\",\"to\":[\"4102804827\",\"4102804828\"],\"from\":\"scsrest\",\"replyexpiry\":60}}");
    } 
    
    @Test
    public void getDeliveryNotificationInvalidTrackingID() throws CMCException {
        stubFor(get(urlMatching("/notifications/[0-9A-Za-z]+"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"5001\",\"message\":\"Tracking ID Not Found -- Notifications AewVvciGlHRM31jg0K.\"}}")));
        
        HttpResponseWrapper<NotificationsResponse> response = messagingApi.getDeliveryNotifications("AewVvciGlHRM31jg0K");
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 404);
        assertEquals(response.getResponseBody().getStatus(), "fail");   
        assertEquals(response.getResponseBody().getCode(), "5001");
        
        // Verify the request
        List<LoggedRequest> requests = findAll(getRequestedFor(urlMatching("/notifications/[0-9A-Za-z]+")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");          
    }
    
    @Test
    public void getDeliveryNotificationValidTrackingID() throws CMCException {
        stubFor(get(urlMatching("/notifications/[0-9A-Za-z]+"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"success\",\"notifications\":{\"to\":[\"4102804827\"],\"from\":\"scsrest\",\"trackinginformation\":[{\"destination\":\"4102804827\",\"messagestatus\":\"Message Accepted\",\"messageID\":\"GW1_AVvciGlHRM32pw0Q\",\"messagetext\":\"Test message\"}]}}}")));        
             
        HttpResponseWrapper<NotificationsResponse> response = messagingApi.getDeliveryNotifications("AewVvciGlHRM31jg0K");
        
        // Verify the response
        assertEquals(response.getHttpStatusCode(), 200);
        Notifications notifications = response.getResponseBody().getNotifications();
        assertEquals(notifications.getFromAddress(),"scsrest");
        assertEquals(notifications.getTo().size(),1);
        assertEquals(notifications.getTo().get(0),"4102804827");
        List<TrackingInformation> trackingInformation = notifications.getTrackingInformation();
        assertEquals(trackingInformation.size(),1);
        assertEquals(trackingInformation.get(0).getDestination(),"4102804827");
        assertEquals(trackingInformation.get(0).getMessageID(),"GW1_AVvciGlHRM32pw0Q");
        assertEquals(trackingInformation.get(0).getMessageText(),"Test message");
        
        // Verify the request
        List<LoggedRequest> requests = findAll(getRequestedFor(urlMatching("/notifications/[0-9A-Za-z]+")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    } 
    
    @Test
    public void getDeliveryReceiptInvalidMessageID() throws CMCException {
        stubFor(get(urlMatching("/receipts/[0-9A-Za-z,]+"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"2003\",\"message\":\"Message ID Not Found -- Receipts AewVvciGlHRM31jg0K.\"}}")));
        
        List<String> messageIds = new ArrayList<String>();
        messageIds.add("AewVvciGlHRM31jg0K");
        HttpResponseWrapper<DeliveryReceiptResponse> response = messagingApi.getDeliveryReceipts(messageIds);

        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 404);
        assertEquals(response.getResponseBody().getStatus(), "fail");   
        assertEquals(response.getResponseBody().getCode(), "2003");
        
        // Verify the request
        List<LoggedRequest> requests = findAll(getRequestedFor(urlMatching("/receipts/[0-9A-Za-z,]+")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");          
    }
    
    @Test
    public void getDeliveryReceiptValidMessageID() throws CMCException {
        stubFor(get(urlMatching("/receipts/[0-9A-Za-z,_]+"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"success\",\"deliveryreceipt\":{\"deliverystatuslist\":[{\"deliverydate\":\"2014-05-28T00:00Z\",\"deliverystatus\":\"Undeliverable by Gateway\",\"messageID\":\"GW1_EwGohZtGQpmh8lGB\",\"to\":\"14106277808\"},{\"deliverydate\":\"2014-06-12T00:00Z\",\"deliverystatus\":\"Delivered to Handset\",\"messageID\":\"GW1_EwBpkTJGkGVEsZ1U\",\"to\":\"14103334444\"}]}}}")));
        
        List<String> messageIds = new ArrayList<String>();
        messageIds.add("GW1_EwGohZtGQpmh8lGB");
        messageIds.add("GW1_EwBpkTJGkGVEsZ1U");
        HttpResponseWrapper<DeliveryReceiptResponse> response = messagingApi.getDeliveryReceipts(messageIds);

        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatus(),"success");
        DeliveryReceipt deliveryReceipt = response.getResponseBody().getDeliveryReceipt();
        List<MessageStatus> deliverystatuslist = deliveryReceipt.getDeliverystatuslist();
        assertEquals(deliverystatuslist.size(),2);
        
        assertEquals(deliverystatuslist.get(0).getMin(),"14106277808");
        assertEquals(deliverystatuslist.get(0).getDeliveryStatus(),"Undeliverable by Gateway");
        assertEquals(deliverystatuslist.get(0).getMessageID(),"GW1_EwGohZtGQpmh8lGB");
        assertEquals(deliverystatuslist.get(0).getDeliveryDate(),"2014-05-28T00:00Z");
        
        assertEquals(deliverystatuslist.get(1).getMin(),"14103334444");
        assertEquals(deliverystatuslist.get(1).getDeliveryStatus(),"Delivered to Handset");
        assertEquals(deliverystatuslist.get(1).getMessageID(),"GW1_EwBpkTJGkGVEsZ1U");
        assertEquals(deliverystatuslist.get(1).getDeliveryDate(),"2014-06-12T00:00Z");

        // Verify the request
        List<LoggedRequest> requests = findAll(getRequestedFor(urlMatching("/receipts/[0-9A-Za-z,_]+")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");          
    }
    
    @Test
    public void getRepliesInvalidMessageID() throws CMCException {
        stubFor(get(urlMatching("/replies/[0-9A-Za-z]+"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"success\",\"replies\":{\"numberofreplies\":0}}}")));
        
        HttpResponseWrapper<MessageRepliesResponse> response = messagingApi.getReplies("AewVvciGlHRM31jg0K");

        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatus(), "success");   
        
        // Verify the request
        List<LoggedRequest> requests = findAll(getRequestedFor(urlMatching("/replies/[0-9A-Za-z]+")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");          
    }
    
    @Test
    public void getRepliesValidMessageID() throws CMCException {
        stubFor(get(urlMatching("/replies/[0-9A-Za-z_]+"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\": \"success\",\"replies\":{\"numberofreplies\": 2,\"replylist\":[{\"from\": \"14106277808\",\"text\": \"Reply back\",\"date\": \"2015-07-13T00:00Z\"},{\"from\": \"14106277809\",\"text\":\"Reply back again\",\"date\":\"2015-09-13T00:00Z\"}]}}}")));
        
        HttpResponseWrapper<MessageRepliesResponse> response = messagingApi.getReplies("GW1_EwGohZtGQpmh8lGB");

        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatus(),"success");
        MessageReplies messageReplies = response.getResponseBody().getMessageReplies();
        List<MessageReply> replylist = messageReplies.getReplies();
        assertEquals(replylist.size(),2);
        
        assertEquals(replylist.get(0).getMin(),"14106277808");
        assertEquals(replylist.get(0).getMsgText(),"Reply back");
        assertEquals(replylist.get(0).getReplyDate(),"2015-07-13T00:00Z");
        
        assertEquals(replylist.get(1).getMin(),"14106277809");
        assertEquals(replylist.get(1).getMsgText(),"Reply back again");
        assertEquals(replylist.get(1).getReplyDate(),"2015-09-13T00:00Z");

        // Verify the request
        List<LoggedRequest> requests = findAll(getRequestedFor(urlMatching("/replies/[0-9A-Za-z_]+")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");          
    }
    
    @Test
    public void getProgramRepliesInvalidProgram() throws CMCException {
        stubFor(get(urlMatching("/programreplies/[0-9A-Za-z]+"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"4003\",\"message\":\"Program not found 1.\"}}")));
        
        ProgramReply programReply = new ProgramReply("1");
        HttpResponseWrapper<MessageRepliesResponse> response = messagingApi.getProgramReplies(programReply);

        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 404);
        assertEquals(response.getResponseBody().getStatus(), "fail");
        assertEquals(response.getResponseBody().getCode(), "4003");
        
        // Verify the request
        List<LoggedRequest> requests = findAll(getRequestedFor(urlMatching("/programreplies/[0-9A-Za-z]+")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");          
    }
    
    @Test
    public void getProgramRepliesValidProgram() throws CMCException {
        stubFor(get(urlMatching("/programreplies/[0-9A-Za-z]+"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\": \"success\",\"replies\":{\"numberofreplies\": 2,\"replylist\":[{\"from\": \"14106277808\",\"text\": \"Reply back\",\"date\": \"2015-07-13T00:00Z\"},{\"from\": \"14106277809\",\"text\":\"Reply back again\",\"date\":\"2015-09-13T00:00Z\"}]}}}")));
        
        ProgramReply programReply = new ProgramReply("GW1EwGohZtGQpmh8lGB");
        HttpResponseWrapper<MessageRepliesResponse> response = messagingApi.getProgramReplies(programReply);

        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatus(),"success");
        MessageReplies messageReplies = response.getResponseBody().getMessageReplies();
        List<MessageReply> replylist = messageReplies.getReplies();
        assertEquals(replylist.size(),2);
        
        assertEquals(replylist.get(0).getMin(),"14106277808");
        assertEquals(replylist.get(0).getMsgText(),"Reply back");
        assertEquals(replylist.get(0).getReplyDate(),"2015-07-13T00:00Z");
        
        assertEquals(replylist.get(1).getMin(),"14106277809");
        assertEquals(replylist.get(1).getMsgText(),"Reply back again");
        assertEquals(replylist.get(1).getReplyDate(),"2015-09-13T00:00Z");
        
        // Verify the request
        List<LoggedRequest> requests = findAll(getRequestedFor(urlMatching("/programreplies/[0-9A-Za-z]+")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");          
    }    
    
    @Test
    public void getProgramRepliesInvalidProgramWithMdns() throws CMCException {
        stubFor(get(urlMatching("/programreplies/[0-9A-Za-z]+/[0-9,]+"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"4003\",\"message\":\"Program not found 1.\"}}")));
        
        ProgramReply programReply = new ProgramReply("1");
        List<String> destinations = new ArrayList<String>();
        destinations.add("14106277808");
        destinations.add("14106277809");
        programReply.setDestinations(destinations);
        HttpResponseWrapper<MessageRepliesResponse> response = messagingApi.getProgramReplies(programReply);

        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 404);
        assertEquals(response.getResponseBody().getStatus(), "fail");
        assertEquals(response.getResponseBody().getCode(), "4003");
        
        // Verify the request
        List<LoggedRequest> requests = findAll(getRequestedFor(urlMatching("/programreplies/[0-9A-Za-z]+/[0-9,]+")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }
    
    @Test
    public void getProgramRepliesValidProgramWithMdns() throws CMCException {
        stubFor(get(urlMatching("/programreplies/[0-9A-Za-z]+/[0-9,]+"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\": \"success\",\"replies\":{\"numberofreplies\": 2,\"replylist\":[{\"from\": \"14106277808\",\"text\": \"Reply back\",\"date\": \"2015-07-13T00:00Z\"},{\"from\": \"14106277809\",\"text\":\"Reply back again\",\"date\":\"2015-09-13T00:00Z\"}]}}}")));
        
        ProgramReply programReply = new ProgramReply("GW1EwGohZtGQpmh8lGB");
        List<String> destinations = new ArrayList<String>();
        destinations.add("14106277808");
        destinations.add("14106277809"); 
        programReply.setDestinations(destinations);
        HttpResponseWrapper<MessageRepliesResponse> response = messagingApi.getProgramReplies(programReply);

        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatus(),"success");
        MessageReplies messageReplies = response.getResponseBody().getMessageReplies();
        List<MessageReply> replylist = messageReplies.getReplies();
        assertEquals(replylist.size(),2);
        
        assertEquals(replylist.get(0).getMin(),"14106277808");
        assertEquals(replylist.get(0).getMsgText(),"Reply back");
        assertEquals(replylist.get(0).getReplyDate(),"2015-07-13T00:00Z");
        
        assertEquals(replylist.get(1).getMin(),"14106277809");
        assertEquals(replylist.get(1).getMsgText(),"Reply back again");
        assertEquals(replylist.get(1).getReplyDate(),"2015-09-13T00:00Z");
        
        // Verify the request
        List<LoggedRequest> requests = findAll(getRequestedFor(urlMatching("/programreplies/[0-9A-Za-z]+/[0-9,]+")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }    
    
    @Test
    public void getProgramRepliesInvalidProgramWithMinutes() throws CMCException {
        stubFor(get(urlPathMatching("/programreplies(.*)"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"4003\",\"message\":\"Program not found 1.\"}}")));
        
        ProgramReply programReply = new ProgramReply("1");
        programReply.setMinutes("7");
        HttpResponseWrapper<MessageRepliesResponse> response = messagingApi.getProgramReplies(programReply);

        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 404);
        assertEquals(response.getResponseBody().getStatus(), "fail");
        assertEquals(response.getResponseBody().getCode(), "4003");
        
        // Verify the request
        List<LoggedRequest> requests = findAll(getRequestedFor(urlPathMatching("/programreplies(.*)")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");          
    }
    
    @Test
    public void getProgramRepliesValidProgramWithMinutes() throws CMCException {    
        stubFor(get(urlPathMatching("/programreplies(.*)"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\": \"success\",\"replies\":{\"numberofreplies\": 2,\"replylist\":[{\"from\": \"14106277808\",\"text\": \"Reply back\",\"date\": \"2015-07-13T00:00Z\"},{\"from\": \"14106277809\",\"text\":\"Reply back again\",\"date\":\"2015-09-13T00:00Z\"}]}}}")));
        
        ProgramReply programReply = new ProgramReply("GW1EwGohZtGQpmh8lGB");
        programReply.setMinutes("7");
        HttpResponseWrapper<MessageRepliesResponse> response = messagingApi.getProgramReplies(programReply);

        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatus(),"success");
        MessageReplies messageReplies = response.getResponseBody().getMessageReplies();
        List<MessageReply> replylist = messageReplies.getReplies();
        assertEquals(replylist.size(),2);
        
        assertEquals(replylist.get(0).getMin(),"14106277808");
        assertEquals(replylist.get(0).getMsgText(),"Reply back");
        assertEquals(replylist.get(0).getReplyDate(),"2015-07-13T00:00Z");
        
        assertEquals(replylist.get(1).getMin(),"14106277809");
        assertEquals(replylist.get(1).getMsgText(),"Reply back again");
        assertEquals(replylist.get(1).getReplyDate(),"2015-09-13T00:00Z");
        
        // Verify the request
        List<LoggedRequest> requests = findAll(getRequestedFor(urlPathMatching("/programreplies(.*)")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");        
    }
    
    @Test
    public void getProgramRepliesInvalidProgramWithMdnsAndMinutes() throws CMCException {
        stubFor(get(urlPathMatching("/programreplies(.*)"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"4003\",\"message\":\"Program not found 1.\"}}")));
        
        ProgramReply programReply = new ProgramReply("1");
        List<String> destinations = new ArrayList<String>();
        destinations.add("14106277808");
        destinations.add("14106277809");
        programReply.setDestinations(destinations);
        programReply.setMinutes("7");
        HttpResponseWrapper<MessageRepliesResponse> response = messagingApi.getProgramReplies(programReply);

        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 404);
        assertEquals(response.getResponseBody().getStatus(), "fail");
        assertEquals(response.getResponseBody().getCode(), "4003");
        
        // Verify the request
        List<LoggedRequest> requests = findAll(getRequestedFor(urlPathMatching("/programreplies(.*)")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");          
    }
    
    @Test
    public void getProgramRepliesValidProgramWithMdnsAndMinutes() throws CMCException {
        stubFor(get(urlPathMatching("/programreplies(.*)"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\": \"success\",\"replies\":{\"numberofreplies\": 2,\"replylist\":[{\"from\": \"14106277808\",\"text\": \"Reply back\",\"date\": \"2015-07-13T00:00Z\"},{\"from\": \"14106277809\",\"text\":\"Reply back again\",\"date\":\"2015-09-13T00:00Z\"}]}}}")));
        
        ProgramReply programReply = new ProgramReply("GW1EwGohZtGQpmh8lGB");
        List<String> destinations = new ArrayList<String>();
        destinations.add("14106277808");
        destinations.add("14106277809"); 
        programReply.setDestinations(destinations);
        programReply.setMinutes("7");
        HttpResponseWrapper<MessageRepliesResponse> response = messagingApi.getProgramReplies(programReply);

        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatus(),"success");
        MessageReplies messageReplies = response.getResponseBody().getMessageReplies();
        List<MessageReply> replylist = messageReplies.getReplies();
        assertEquals(replylist.size(),2);
        
        assertEquals(replylist.get(0).getMin(),"14106277808");
        assertEquals(replylist.get(0).getMsgText(),"Reply back");
        assertEquals(replylist.get(0).getReplyDate(),"2015-07-13T00:00Z");
        
        assertEquals(replylist.get(1).getMin(),"14106277809");
        assertEquals(replylist.get(1).getMsgText(),"Reply back again");
        assertEquals(replylist.get(1).getReplyDate(),"2015-09-13T00:00Z");
        
        // Verify the request
        List<LoggedRequest> requests = findAll(getRequestedFor(urlPathMatching("/programreplies(.*)")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");          
    }    
    
}
