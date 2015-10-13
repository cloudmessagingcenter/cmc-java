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
import com.telecomsys.cmc.exception.CMCServerException;
import com.telecomsys.cmc.http.HttpResponseWrapper;
import com.telecomsys.cmc.model.Message;
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
    
}