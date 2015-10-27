package com.telecomsys.cmc;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.telecomsys.cmc.api.SchedulingApi;
import com.telecomsys.cmc.exception.CMCAuthenticationException;
import com.telecomsys.cmc.exception.CMCException;
import com.telecomsys.cmc.exception.CMCIOException;
import com.telecomsys.cmc.exception.CMCClientException;
import com.telecomsys.cmc.http.HttpResponseWrapper;
import com.telecomsys.cmc.model.Message;
import com.telecomsys.cmc.model.Schedule;
import com.telecomsys.cmc.model.ScheduleMessage;
import com.telecomsys.cmc.response.RestResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

public class SchedulingApiTest {
    
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
    private SchedulingApi schedulingApi;
    
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(18089);
    
    @Before
    public void setup() {
        schedulingApi = new SchedulingApi("http://localhost:18089", USERNAME, PASSWORD);
    }
    
    @Test(expected=CMCIOException.class)
    public void invalidHostTest() throws CMCException {
        SchedulingApi invalidSchedulingApi = new SchedulingApi("http://invalidHost:1234", USERNAME, PASSWORD);
        List<String> messageIds = new ArrayList<String>();
        messageIds.add("GW1EwGohZtGQpmh8lGB");   
        invalidSchedulingApi.deleteScheduledMessages(messageIds);        
    }
    
    @Test(expected=CMCAuthenticationException.class)
    public void invalidCredentialsTest() throws CMCException {
        stubFor(delete(urlMatching("/schedules/[0-9A-Za-z,]+"))
                .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader("Content-Type", "text/html")
                    .withBody("This request requires HTTP authentication.")));
        
        List<String> messageIds = new ArrayList<String>();
        messageIds.add("GW1EwGohZtGQpmh8lGB");
        schedulingApi.deleteScheduledMessages(messageIds);
    }
    
    @Test
    public void deleteSingleValidSchedule() throws CMCException {
        stubFor(delete(urlMatching("/schedules/[0-9,]+"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\": {\"status\": \"success\"}}")));
        
        List<String> schedules = new ArrayList<String>();
        schedules.add("111000000066448");
        HttpResponseWrapper<RestResponse> response = schedulingApi.deleteScheduledMessages(schedules);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(deleteRequestedFor(urlMatching("/schedules/[0-9,]+")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }

    @Test
    public void deleteSingleInValidSchedule() throws CMCException {
        stubFor(delete(urlMatching("/schedules/[0-9,]+"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"9105\",\"message\":\"Some or all of the contacts with scheduled messages 111000000066448 not found.\"}}")));
        
        List<String> schedules = new ArrayList<String>();
        schedules.add("111000000066448");
        HttpResponseWrapper<RestResponse> response = schedulingApi.deleteScheduledMessages(schedules);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 404);
        assertEquals(response.getResponseBody().getCode(), "9105");
        assertEquals(response.getResponseBody().getStatus(), "fail");   

        // Verify the request
        List<LoggedRequest> requests = findAll(deleteRequestedFor(urlMatching("/schedules/[0-9,]+")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }
    
    @Test
    public void deleteMultipleValidSchedule() throws CMCException {
        stubFor(delete(urlMatching("/schedules/[0-9,]+"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\": {\"status\": \"success\"}}")));
        
        List<String> schedules = new ArrayList<String>();
        schedules.add("111000000066448");
        schedules.add("111000000066449");
        HttpResponseWrapper<RestResponse> response = schedulingApi.deleteScheduledMessages(schedules);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(deleteRequestedFor(urlMatching("/schedules/[0-9,]+")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }     
    
    @Test
    public void deleteMultipleInValidSchedules() throws CMCException {
        stubFor(delete(urlMatching("/schedules/[0-9,]+"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"9105\",\"message\":\"Some or all of the contacts with scheduled messages 111000000066448, 111000000066449 not found.\"}}")));
        
        List<String> schedules = new ArrayList<String>();
        schedules.add("111000000066448");
        schedules.add("111000000066449");       
        HttpResponseWrapper<RestResponse> response = schedulingApi.deleteScheduledMessages(schedules);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 404);
        assertEquals(response.getResponseBody().getStatus(), "fail");   
        assertEquals(response.getResponseBody().getCode(), "9105");

        // Verify the request
        List<LoggedRequest> requests = findAll(deleteRequestedFor(urlMatching("/schedules/[0-9,]+")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }
    
    @Test
    public void deleteAllSchedules() throws CMCException {
        stubFor(delete(urlPathMatching("/schedules*"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\": {\"status\": \"success\"}}")));
        
        HttpResponseWrapper<RestResponse> response = schedulingApi.deleteAllScheduledMessages();
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(deleteRequestedFor(urlPathMatching("/schedules*")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }
    
    @Test
    public void scheduleMessagesSingleDestinationInvalidDate() throws CMCException {
        stubFor(post(urlEqualTo("/schedules"))
                .willReturn(aResponse()
                    .withStatus(400)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"9004\",\"message\":\"Scheduled event with name Test schedule start date is in the past. Please try again with correct start date.\"}}")));        
        
        List<String> destinations = new ArrayList<String>();
        destinations.add("410333444"); 
        Message message = new Message(destinations, REST_CONNECTION_KEYWORD, "Test schedule");
        message.setSubject("Test");
        
        Schedule schedule = new Schedule();
        schedule.setJobName("Test schedule");
        schedule.setRecurrence("weekly");
        schedule.setStartDate("2015-06-20T12:46-04");
        schedule.setExpireDate("2015-07-29T18:46-04");
        ScheduleMessage schedulMessage = new ScheduleMessage();
        schedulMessage.setMessage(message);
        schedulMessage.setSchedule(schedule);
        
        try {
            schedulingApi.scheduleMessage(schedulMessage);
        } catch (CMCClientException cmex) {
            RestResponse error = cmex.getError();
            assertEquals(error.getStatus(), "fail");
            assertEquals(error.getCode(), "9004");
        }
        
        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlMatching("/schedules")));
        assertEquals(requests.size(), 1);
        assertTrue(requests.get(0).getBodyAsString().equals("{\"schedulemessage\":{\"message\":{\"message\":\"Test schedule\",\"subject\":\"Test\",\"to\":[\"410333444\"],\"from\":\"scsrest\"},\"schedule\":{\"recurrence\":\"weekly\",\"startdate\":\"2015-06-20T12:46-04\",\"enddate\":\"2015-07-29T18:46-04\",\"name\":\"Test schedule\"}}}") ||
                requests.get(0).getBodyAsString().equals("{\"schedulemessage\":{\"schedule\":{\"recurrence\":\"weekly\",\"startdate\":\"2015-06-20T12:46-04\",\"enddate\":\"2015-07-29T18:46-04\",\"name\":\"Test schedule\"},\"message\":{\"message\":\"Test schedule\",\"subject\":\"Test\",\"to\":[\"410333444\"],\"from\":\"scsrest\"}}}"));
    }
    
    @Test
    public void scheduleMessagesSingleDestinationInvalidFrom() throws CMCException {
        stubFor(post(urlEqualTo("/schedules"))
                .willReturn(aResponse()
                    .withStatus(400)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"9017\",\"message\":\"Your message failed: Invalid from address.\"}}")));        
        
        List<String> destinations = new ArrayList<String>();
        destinations.add("410333444"); 
        Message message = new Message(destinations, REST_CONNECTION_KEYWORD, "Test schedule");
        message.setSubject("Test");
        
        Schedule schedule = new Schedule();
        schedule.setJobName("Test schedule");
        schedule.setRecurrence("weekly");
        schedule.setStartDate("2015-11-20T12:46-04");
        schedule.setExpireDate("2016-07-29T18:46-04");
        ScheduleMessage schedulMessage = new ScheduleMessage();
        schedulMessage.setMessage(message);
        schedulMessage.setSchedule(schedule);
        
        try {
            schedulingApi.scheduleMessage(schedulMessage);
        } catch (CMCClientException cmex) {
            RestResponse error = cmex.getError();
            assertEquals(error.getStatus(), "fail");
            assertEquals(error.getCode(), "9017");
        }
        
        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlMatching("/schedules")));
        assertEquals(requests.size(), 1);
        assertTrue(requests.get(0).getBodyAsString().equals("{\"schedulemessage\":{\"message\":{\"message\":\"Test schedule\",\"subject\":\"Test\",\"to\":[\"410333444\"],\"from\":\"scsrest\"},\"schedule\":{\"recurrence\":\"weekly\",\"startdate\":\"2015-11-20T12:46-04\",\"enddate\":\"2016-07-29T18:46-04\",\"name\":\"Test schedule\"}}}") ||
                requests.get(0).getBodyAsString().equals("{\"schedulemessage\":{\"schedule\":{\"recurrence\":\"weekly\",\"startdate\":\"2015-11-20T12:46-04\",\"enddate\":\"2016-07-29T18:46-04\",\"name\":\"Test schedule\"},\"message\":{\"message\":\"Test schedule\",\"subject\":\"Test\",\"to\":[\"410333444\"],\"from\":\"scsrest\"}}}"));
    }
    
    @Test
    public void scheduleMessagesSingleDestinationValid() throws CMCException {
        stubFor(post(urlEqualTo("/schedules"))
                .willReturn(aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"schedulemessage\":{\"messageID\":11100000103313,\"message\":{\"to\":[\"410333444\"]},\"schedule\":{\"recurrence\":\"weekly\",\"startdate\":\"2015-11-20T12:50-04\",\"enddate\":\"2016-07-29T18:50-04\",\"name\":\"Test schedule\"}}}")));        
        
        List<String> destinations = new ArrayList<String>();
        destinations.add("410333444"); 
        Message message = new Message(destinations, REST_CONNECTION_KEYWORD, "Test schedule");
        message.setSubject("Test");
        
        Schedule schedule = new Schedule();
        schedule.setJobName("Test schedule");
        schedule.setRecurrence("weekly");
        schedule.setStartDate("2015-11-20T12:46-04");
        schedule.setExpireDate("2016-07-29T18:46-04");
        ScheduleMessage schedulMessage = new ScheduleMessage();
        schedulMessage.setMessage(message);
        schedulMessage.setSchedule(schedule);      
        
        HttpResponseWrapper<ScheduleMessage> response = schedulingApi.scheduleMessage(schedulMessage);

        // Verify the response
        assertEquals(response.getHttpStatusCode(), 201);
        Schedule scheduleResponse = response.getResponseBody().getSchedule();
        Message messageResponse = response.getResponseBody().getMessage();
        Long messageId = response.getResponseBody().getMessageId();
        
        assertEquals(messageId, new Long(11100000103313L));
        assertEquals(scheduleResponse.getJobName(), "Test schedule");
        assertEquals(scheduleResponse.getExpireDate(), "2016-07-29T18:50-04");
        assertEquals(scheduleResponse.getStartDate(), "2015-11-20T12:50-04");
        assertEquals(scheduleResponse.getRecurrence(), "weekly");
        List<String> recipients = messageResponse.getDestinations();
        assertEquals(recipients.size(), 1);
        assertEquals(recipients.get(0), "410333444");
        
        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlMatching("/schedules")));
        assertEquals(requests.size(), 1);
        assertTrue(requests.get(0).getBodyAsString().equals("{\"schedulemessage\":{\"message\":{\"message\":\"Test schedule\",\"subject\":\"Test\",\"to\":[\"410333444\"],\"from\":\"scsrest\"},\"schedule\":{\"recurrence\":\"weekly\",\"startdate\":\"2015-11-20T12:46-04\",\"enddate\":\"2016-07-29T18:46-04\",\"name\":\"Test schedule\"}}}") ||
                requests.get(0).getBodyAsString().equals("{\"schedulemessage\":{\"schedule\":{\"recurrence\":\"weekly\",\"startdate\":\"2015-11-20T12:46-04\",\"enddate\":\"2016-07-29T18:46-04\",\"name\":\"Test schedule\"},\"message\":{\"message\":\"Test schedule\",\"subject\":\"Test\",\"to\":[\"410333444\"],\"from\":\"scsrest\"}}}"));
    }
    
    @Test
    public void scheduleMessagesMultipleDestinationValid() throws CMCException {
        stubFor(post(urlEqualTo("/schedules"))
                .willReturn(aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"schedulemessage\":{\"messageID\":11100000103313,\"message\":{\"to\":[\"410333444\",\"410333445\"]},\"schedule\":{\"recurrence\":\"weekly\",\"startdate\":\"2015-11-20T12:50-04\",\"enddate\":\"2016-07-29T18:50-04\",\"name\":\"Test schedule\"}}}")));        
        
        List<String> destinations = new ArrayList<String>();
        destinations.add("410333444");
        destinations.add("410333445");
        Message message = new Message(destinations, REST_CONNECTION_KEYWORD, "Test schedule");
        message.setSubject("Test");
        
        Schedule schedule = new Schedule();
        schedule.setJobName("Test schedule");
        schedule.setRecurrence("weekly");
        schedule.setStartDate("2015-11-20T12:46-04");
        schedule.setExpireDate("2016-07-29T18:46-04");
        ScheduleMessage schedulMessage = new ScheduleMessage();
        schedulMessage.setMessage(message);
        schedulMessage.setSchedule(schedule);      
        
        HttpResponseWrapper<ScheduleMessage> response = schedulingApi.scheduleMessage(schedulMessage);

        // Verify the response
        assertEquals(response.getHttpStatusCode(), 201);
        Schedule scheduleResponse = response.getResponseBody().getSchedule();
        Message messageResponse = response.getResponseBody().getMessage();
        Long messageId = response.getResponseBody().getMessageId();
        
        assertEquals(messageId, new Long(11100000103313L));
        assertEquals(scheduleResponse.getJobName(), "Test schedule");
        assertEquals(scheduleResponse.getExpireDate(), "2016-07-29T18:50-04");
        assertEquals(scheduleResponse.getStartDate(), "2015-11-20T12:50-04");
        assertEquals(scheduleResponse.getRecurrence(), "weekly");
        List<String> recipients = messageResponse.getDestinations();
        assertEquals(recipients.size(), 2);
        assertEquals(recipients.get(0), "410333444");
        assertEquals(recipients.get(1), "410333445");
        
        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlMatching("/schedules")));
        assertEquals(requests.size(), 1);
        assertTrue(requests.get(0).getBodyAsString().equals("{\"schedulemessage\":{\"message\":{\"message\":\"Test schedule\",\"subject\":\"Test\",\"to\":[\"410333444\",\"410333445\"],\"from\":\"scsrest\"},\"schedule\":{\"recurrence\":\"weekly\",\"startdate\":\"2015-11-20T12:46-04\",\"enddate\":\"2016-07-29T18:46-04\",\"name\":\"Test schedule\"}}}") ||
                requests.get(0).getBodyAsString().equals("{\"schedulemessage\":{\"schedule\":{\"recurrence\":\"weekly\",\"startdate\":\"2015-11-20T12:46-04\",\"enddate\":\"2016-07-29T18:46-04\",\"name\":\"Test schedule\"},\"message\":{\"message\":\"Test schedule\",\"subject\":\"Test\",\"to\":[\"410333444\",\"410333445\"],\"from\":\"scsrest\"}}}"));
 
    }        
    
}