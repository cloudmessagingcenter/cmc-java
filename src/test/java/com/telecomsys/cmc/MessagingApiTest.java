package com.telecomsys.cmc;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.telecomsys.cmc.api.MessagingApi;
import com.telecomsys.cmc.exception.CMCAuthenticationException;
import com.telecomsys.cmc.exception.CMCException;
import com.telecomsys.cmc.exception.CMCIOException;
import com.telecomsys.cmc.exception.CMCServerException;
import com.telecomsys.cmc.response.RestResponse;

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
        invalidMessagingApi.sendMessage("4102804827", REST_CONNECTION_KEYWORD, "Test message");
    }
    
    @Test(expected=CMCAuthenticationException.class)
    public void invalidCredentialsTest() throws CMCException {
        stubFor(post(urlEqualTo("/messages"))
                .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader("Content-Type", "text/html")
                    .withBody("This request requires HTTP authentication.")));
        
        messagingApi.sendMessage("4102804827", REST_CONNECTION_KEYWORD, "Test message");
    }     
    
    @Test
    public void invalidKeywordTest() throws CMCException {
        stubFor(post(urlEqualTo("/messages"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"1010\",\"message\":\"Your message failed: Invalid from address.\"}}")));        
        
        try { 
            messagingApi.sendMessage("4102804827", REST_CONNECTION_KEYWORD, "Test message");
        } catch (CMCServerException cmex) {
            RestResponse error = cmex.getError();
            assertEquals(error.getStatus(), "fail");
            assertEquals(error.getCode(), "1010");
            assertEquals(error.getMessage(), "Your message failed: Invalid from address.");
        }
    }
    
}
