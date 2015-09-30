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
import com.telecomsys.cmc.exception.CMCServerException;
import com.telecomsys.cmc.http.HttpResponseWrapper;
import com.telecomsys.cmc.model.Message;
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
        } catch (CMCServerException cmex) {
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
        assertEquals(requests.get(0).getBodyAsString(), "{\"sendmessage\":{\"message\":\"Test message\",\"to\":[\"4102804827\",\"4102804828\"],\"notifyURL\":\"http://customer.com/notifications\",\"replyexpiry\":60,\"from\":\"scsrest\"}}");
    }    
    
}
