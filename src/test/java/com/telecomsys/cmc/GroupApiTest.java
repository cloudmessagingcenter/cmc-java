package com.telecomsys.cmc;

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.telecomsys.cmc.api.GroupApi;
import com.telecomsys.cmc.exception.CMCAuthenticationException;
import com.telecomsys.cmc.exception.CMCException;
import com.telecomsys.cmc.exception.CMCIOException;
import com.telecomsys.cmc.http.HttpResponseWrapper;
import com.telecomsys.cmc.model.Group;
import com.telecomsys.cmc.response.RestResponse;
import com.telecomsys.cmc.response.StatusResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

public class GroupApiTest {
    /**
     * CMC REST user name (account ID).
     */
    private static final String USERNAME = "9876";

    /**
     * CMC REST user name (account ID).
     */
    private static final String PASSWORD = "1234";
    
    /**
     * CMC client instance.
     */
    private GroupApi groupApi;
    
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(18089);
    
    @Before
    public void setup() {
        groupApi = new GroupApi("http://localhost:18089", USERNAME, PASSWORD);
    }  
    
    @Test(expected=CMCIOException.class)
    public void invalidHostTest() throws CMCException {
        GroupApi invalidGroupApi = new GroupApi("http://invalidHost:1234", USERNAME, PASSWORD);
        Group group = new Group();
        group.setName("Test group");
        group.setDescription("Test group");
        invalidGroupApi.addGroup(group);
    }
    
    @Test(expected=CMCAuthenticationException.class)
    public void invalidCredentialsTest() throws CMCException {
        stubFor(post(urlEqualTo("/groups"))
                .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader("Content-Type", "text/html")
                    .withBody("This request requires HTTP authentication.")));
        
        Group group = new Group();
        group.setName("Test group");
        group.setDescription("Test group");
        groupApi.addGroup(group);
    }
    
    @Test
    public void addGroupNoMembers() throws CMCException {
        stubFor(post(urlEqualTo("/groups"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\": {\"status\": \"success\"}}")));
        
        Group group = new Group();
        group.setName("Test1");
        group.setDescription("Test group");
        HttpResponseWrapper<RestResponse> response = groupApi.addGroup(group);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlMatching("/groups")));
        assertEquals(requests.size(), 1);
        System.out.println(requests.get(0).getBodyAsString());
        assertEquals(requests.get(0).getBodyAsString(), "{\"groups\":{\"groupname\":\"Test1\",\"groupdesc\":\"Test group\"}}");
    } 

    @Test
    public void addGroupMultipleMembers() throws CMCException {
        stubFor(post(urlEqualTo("/groups"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\": {\"status\": \"success\"}}")));
        
        Group group = new Group();
        group.setName("Test1");
        group.setDescription("Test group");
        HttpResponseWrapper<RestResponse> response = groupApi.addGroup(group);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlMatching("/groups")));
        assertEquals(requests.size(), 1);
        System.out.println(requests.get(0).getBodyAsString());
        assertEquals(requests.get(0).getBodyAsString(), "{\"groups\":{\"groupname\":\"Test1\",\"groupdesc\":\"Test group\"}}");
    }
    
}