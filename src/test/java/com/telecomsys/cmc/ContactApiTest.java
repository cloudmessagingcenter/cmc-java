package com.telecomsys.cmc;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.telecomsys.cmc.api.ContactApi;
import com.telecomsys.cmc.exception.CMCAuthenticationException;
import com.telecomsys.cmc.exception.CMCException;
import com.telecomsys.cmc.exception.CMCIOException;
import com.telecomsys.cmc.http.HttpResponseWrapper;
import com.telecomsys.cmc.model.Contact;
import com.telecomsys.cmc.response.ContactsResponse;
import com.telecomsys.cmc.response.RestResponse;
import com.telecomsys.cmc.response.StatusResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

public class ContactApiTest {
    
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
    private ContactApi contactApi;
    
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(18089);
    
    @Before
    public void setup() {
        contactApi = new ContactApi("http://localhost:18089", USERNAME, PASSWORD);
    }  
    
    @Test(expected=CMCIOException.class)
    public void invalidHostTest() throws CMCException {
        ContactApi invalidContactApi = new ContactApi("http://invalidHost:1234", USERNAME, PASSWORD);
        List<Contact> contacts = new ArrayList<Contact>();
        contacts.add(new Contact("4102804827", "John", "Doe"));
        invalidContactApi.addContacts(contacts);
    }
    
    @Test(expected=CMCAuthenticationException.class)
    public void invalidCredentialsTest() throws CMCException {
        stubFor(post(urlEqualTo("/contacts"))
                .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader("Content-Type", "text/html")
                    .withBody("This request requires HTTP authentication.")));
        
        List<Contact> contacts = new ArrayList<Contact>();
        contacts.add(new Contact("4102804827", "John", "Doe"));
        contactApi.addContacts(contacts);
    }
    
    @Test
    public void addSingleValidContact() throws CMCException {
        stubFor(post(urlEqualTo("/contacts"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"statusList\":[{\"status\":\"success\"}]}}")));
        
        List<Contact> contacts = new ArrayList<Contact>();
        contacts.add(new Contact("14102718101", "John", "Doe"));
        HttpResponseWrapper<StatusResponse> response = contactApi.addContacts(contacts);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatusResponses().size(), 1);
        assertEquals(response.getResponseBody().getStatusResponses().get(0).getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlMatching("/contacts")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"contactList\":{\"contacts\":[{\"mdn\":\"14102718101\",\"first\":\"John\",\"last\":\"Doe\"}]}}");
    }
    
    @Test
    public void addSingleInValidContact() throws CMCException {
        stubFor(post(urlEqualTo("/contacts"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"statusList\":[{\"status\":\"fail\",\"code\":\"8003\",\"message\":\"Contact with cell number 14102718102 already exists.\"}]}}")));
        
        List<Contact> contacts = new ArrayList<Contact>();
        contacts.add(new Contact("14102718101", "John", "Doe"));
        HttpResponseWrapper<StatusResponse> response = contactApi.addContacts(contacts);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatusResponses().size(), 1);
        assertEquals(response.getResponseBody().getStatusResponses().get(0).getStatus(), "fail");   
        assertEquals(response.getResponseBody().getStatusResponses().get(0).getCode(), "8003");   

        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlMatching("/contacts")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"contactList\":{\"contacts\":[{\"mdn\":\"14102718101\",\"first\":\"John\",\"last\":\"Doe\"}]}}");
    }     
    
    @Test
    public void addMultipleValidContact() throws CMCException {
        stubFor(post(urlEqualTo("/contacts"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"statusList\":[{\"status\":\"success\"},{\"status\":\"success\"}]}}")));
        
        List<Contact> contacts = new ArrayList<Contact>();
        contacts.add(new Contact("14102718102", "Jane", "Snow"));
        contacts.add(new Contact("14102718103", "Jack", "Cold"));
        HttpResponseWrapper<StatusResponse> response = contactApi.addContacts(contacts);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatusResponses().size(), 2);
        assertEquals(response.getResponseBody().getStatusResponses().get(0).getStatus(), "success");   
        assertEquals(response.getResponseBody().getStatusResponses().get(1).getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlMatching("/contacts")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"contactList\":{\"contacts\":[{\"mdn\":\"14102718102\",\"first\":\"Jane\",\"last\":\"Snow\"},{\"mdn\":\"14102718103\",\"first\":\"Jack\",\"last\":\"Cold\"}]}}");
    }  
    
    @Test
    public void addMultipleInValidContact() throws CMCException {
        stubFor(post(urlEqualTo("/contacts"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"statusList\":[{\"status\":\"error\",\"code\":\"8003\",\"message\":\"Contact with cell number 14102718102 already exists.\"},{\"status\":\"error\",\"code\":\"8003\",\"message\":\"Contact with cell number 14102718103 already exists.\"}]}}")));
        
        List<Contact> contacts = new ArrayList<Contact>();
        contacts.add(new Contact("14102718102", "Jane", "Snow"));
        contacts.add(new Contact("14102718103", "Jack", "Cold"));
        HttpResponseWrapper<StatusResponse> response = contactApi.addContacts(contacts);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatusResponses().size(), 2);
        assertEquals(response.getResponseBody().getStatusResponses().get(0).getStatus(), "error");   
        assertEquals(response.getResponseBody().getStatusResponses().get(1).getStatus(), "error");   
        assertEquals(response.getResponseBody().getStatusResponses().get(0).getCode(), "8003");   
        assertEquals(response.getResponseBody().getStatusResponses().get(1).getCode(), "8003");   

        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlMatching("/contacts")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"contactList\":{\"contacts\":[{\"mdn\":\"14102718102\",\"first\":\"Jane\",\"last\":\"Snow\"},{\"mdn\":\"14102718103\",\"first\":\"Jack\",\"last\":\"Cold\"}]}}");
    }
    
    @Test
    public void updateSingleValidContact() throws CMCException {
        stubFor(put(urlEqualTo("/contacts"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"statusList\":[{\"status\":\"success\"}]}}")));
        
        List<Contact> contacts = new ArrayList<Contact>();
        contacts.add(new Contact("14102718101", "John", "Doe"));
        HttpResponseWrapper<StatusResponse> response = contactApi.updateContacts(contacts);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatusResponses().size(), 1);
        assertEquals(response.getResponseBody().getStatusResponses().get(0).getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(putRequestedFor(urlMatching("/contacts")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"contactList\":{\"contacts\":[{\"mdn\":\"14102718101\",\"first\":\"John\",\"last\":\"Doe\"}]}}");
    }
    
    @Test
    public void updateSingleInValidContact() throws CMCException {
        stubFor(put(urlEqualTo("/contacts"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"statusList\":[{\"status\":\"error\",\"code\":\"8003\",\"message\":\"Contact with cell number 14102718102 already exists.\"}]}}")));
        
        List<Contact> contacts = new ArrayList<Contact>();
        contacts.add(new Contact("14102718101", "John", "Doe"));
        HttpResponseWrapper<StatusResponse> response = contactApi.updateContacts(contacts);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatusResponses().size(), 1);
        assertEquals(response.getResponseBody().getStatusResponses().get(0).getStatus(), "error");   
        assertEquals(response.getResponseBody().getStatusResponses().get(0).getCode(), "8003");   

        // Verify the request
        List<LoggedRequest> requests = findAll(putRequestedFor(urlMatching("/contacts")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"contactList\":{\"contacts\":[{\"mdn\":\"14102718101\",\"first\":\"John\",\"last\":\"Doe\"}]}}");
    }
    
    @Test
    public void updateMultipleValidContact() throws CMCException {
        stubFor(put(urlEqualTo("/contacts"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"statusList\":[{\"status\":\"success\"},{\"status\":\"success\"}]}}")));
        
        List<Contact> contacts = new ArrayList<Contact>();
        contacts.add(new Contact("14102718102", "Jane", "Snow"));
        contacts.add(new Contact("14102718103", "Jack", "Cold"));
        HttpResponseWrapper<StatusResponse> response = contactApi.updateContacts(contacts);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatusResponses().size(), 2);
        assertEquals(response.getResponseBody().getStatusResponses().get(0).getStatus(), "success");   
        assertEquals(response.getResponseBody().getStatusResponses().get(1).getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(putRequestedFor(urlMatching("/contacts")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"contactList\":{\"contacts\":[{\"mdn\":\"14102718102\",\"first\":\"Jane\",\"last\":\"Snow\"},{\"mdn\":\"14102718103\",\"first\":\"Jack\",\"last\":\"Cold\"}]}}");
    }  
    
    @Test
    public void updateMultipleInValidContact() throws CMCException {
        stubFor(put(urlEqualTo("/contacts"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"statusList\":[{\"status\":\"error\",\"code\":\"8003\",\"message\":\"Contact with cell number 14102718102 already exists.\"},{\"status\":\"error\",\"code\":\"8003\",\"message\":\"Contact with cell number 14102718103 already exists.\"}]}}")));
        
        List<Contact> contacts = new ArrayList<Contact>();
        contacts.add(new Contact("14102718102", "Jane", "Snow"));
        contacts.add(new Contact("14102718103", "Jack", "Cold"));
        HttpResponseWrapper<StatusResponse> response = contactApi.updateContacts(contacts);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatusResponses().size(), 2);
        assertEquals(response.getResponseBody().getStatusResponses().get(0).getStatus(), "error");   
        assertEquals(response.getResponseBody().getStatusResponses().get(1).getStatus(), "error");   
        assertEquals(response.getResponseBody().getStatusResponses().get(0).getCode(), "8003");   
        assertEquals(response.getResponseBody().getStatusResponses().get(1).getCode(), "8003");   

        // Verify the request
        List<LoggedRequest> requests = findAll(putRequestedFor(urlMatching("/contacts")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"contactList\":{\"contacts\":[{\"mdn\":\"14102718102\",\"first\":\"Jane\",\"last\":\"Snow\"},{\"mdn\":\"14102718103\",\"first\":\"Jack\",\"last\":\"Cold\"}]}}");
    }
    
    @Test
    public void deleteSingleValidContact() throws CMCException {
        stubFor(delete(urlMatching("/contacts/[0-9,]+"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\": {\"status\": \"success\"}}")));
        
        List<String> mdns = new ArrayList<String>();
        mdns.add("14102718101");
        HttpResponseWrapper<RestResponse> response = contactApi.deleteContacts(mdns);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(deleteRequestedFor(urlMatching("/contacts/[0-9,]+")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }
    
    @Test
    public void deleteSingleInValidContact() throws CMCException {
        stubFor(delete(urlMatching("/contacts/[0-9,]+"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"8103\",\"message\":\"Some or all of the contacts with mdns 14102718101 not found.\"}}")));
        
        List<String> mdns = new ArrayList<String>();
        mdns.add("14102718101");
        HttpResponseWrapper<RestResponse> response = contactApi.deleteContacts(mdns);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 404);
        assertEquals(response.getResponseBody().getCode(), "8103");
        assertEquals(response.getResponseBody().getStatus(), "fail");   

        // Verify the request
        List<LoggedRequest> requests = findAll(deleteRequestedFor(urlMatching("/contacts/[0-9,]+")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }    
    
    @Test
    public void deleteMultipleValidContact() throws CMCException {
        stubFor(delete(urlMatching("/contacts/[0-9,]+"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\": {\"status\": \"success\"}}")));
        
        List<String> mdns = new ArrayList<String>();
        mdns.add("14102718101");
        mdns.add("14102718102");
        HttpResponseWrapper<RestResponse> response = contactApi.deleteContacts(mdns);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(deleteRequestedFor(urlMatching("/contacts/[0-9,]+")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }   
    
    @Test
    public void deleteMultipleInValidContact() throws CMCException {
        stubFor(delete(urlMatching("/contacts/[0-9,]+"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"8103\",\"message\":\"Some or all of the contacts with mdns 14102718101,14102718102 not found.\"}}")));
        
        List<String> mdns = new ArrayList<String>();
        mdns.add("14102718101");
        mdns.add("14102718102");
        HttpResponseWrapper<RestResponse> response = contactApi.deleteContacts(mdns);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 404);
        assertEquals(response.getResponseBody().getStatus(), "fail");   
        assertEquals(response.getResponseBody().getCode(), "8103");

        // Verify the request
        List<LoggedRequest> requests = findAll(deleteRequestedFor(urlMatching("/contacts/[0-9,]+")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }  
    
    @Test
    public void deleteAllContacts() throws CMCException {
        stubFor(delete(urlPathMatching("/contacts*"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\": {\"status\": \"success\"}}")));
        
        HttpResponseWrapper<RestResponse> response = contactApi.deleteAllContacts();
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(deleteRequestedFor(urlPathMatching("/contacts*")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }    
    
    @Test
    public void retrieveSingleValidContact() throws CMCException {
        stubFor(get(urlMatching("/contacts/[0-9,]+"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"contactList\":[{\"mdn\":\"14102718101\",\"first\":\"John\",\"last\":\"Doe\",\"email\":\"test@test.com\",\"org\":\"my new org\",\"shared\":false,\"smartPhone\":false,\"country\":\"USA\"}]}}")));
        
        List<String> mdns = new ArrayList<String>();
        mdns.add("14102718101");
        HttpResponseWrapper<ContactsResponse> response = contactApi.retrieveContacts(mdns);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        
        List<Contact> contacts = response.getResponseBody().getContactsResponses();
        assertEquals(contacts.size(), 1);
        
        Contact contact = contacts.get(0);
        assertEquals(contact.getFirstName(), "John");
        assertEquals(contact.getLastName(), "Doe");
        assertEquals(contact.getEmailAddress(), "test@test.com");

        // Verify the request
        List<LoggedRequest> requests = findAll(getRequestedFor(urlMatching("/contacts/[0-9,]+")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    } 
    
    @Test
    public void retrieveSingleInValidContact() throws CMCException {
        stubFor(get(urlMatching("/contacts/[0-9,]+"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"8203\",\"message\":\"Contact with the mdn 14102718101 could not be found.\"}}")));
        
        List<String> mdns = new ArrayList<String>();
        mdns.add("14102718101");
        HttpResponseWrapper<ContactsResponse> response = contactApi.retrieveContacts(mdns);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 404);
        assertEquals(response.getResponseBody().getStatus(), "fail");   
        assertEquals(response.getResponseBody().getCode(), "8203");   
        
        List<Contact> contacts = response.getResponseBody().getContactsResponses();
        assertEquals(contacts, null);

        // Verify the request
        List<LoggedRequest> requests = findAll(getRequestedFor(urlMatching("/contacts/[0-9,]+")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }
    
    @Test
    public void retrieveMultipleValidContact() throws CMCException {
        stubFor(get(urlMatching("/contacts/[0-9,]+"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"contactList\":[{\"mdn\":\"14102718101\",\"first\":\"John\",\"last\":\"Doe\",\"email\":\"test@test.com\",\"org\":\"my new org\",\"shared\":false,\"smartPhone\":false,\"country\":\"USA\"},{\"mdn\":\"14102718102\",\"first\":\"Jane\",\"last\":\"Doe\",\"email\":\"test@test.com\",\"org\":\"my new org\",\"shared\":false,\"smartPhone\":false,\"country\":\"USA\"}]}}")));
        
        List<String> mdns = new ArrayList<String>();
        mdns.add("14102718101");
        mdns.add("14102718102");
        HttpResponseWrapper<ContactsResponse> response = contactApi.retrieveContacts(mdns);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        
        List<Contact> contacts = response.getResponseBody().getContactsResponses();
        assertEquals(contacts.size(), 2);
        
        Contact contact = contacts.get(0);
        assertEquals(contact.getFirstName(), "John");
        assertEquals(contact.getLastName(), "Doe");
        assertEquals(contact.getEmailAddress(), "test@test.com");
        
        contact = contacts.get(1);
        assertEquals(contact.getFirstName(), "Jane");
        assertEquals(contact.getLastName(), "Doe");
        assertEquals(contact.getEmailAddress(), "test@test.com");        

        // Verify the request
        List<LoggedRequest> requests = findAll(getRequestedFor(urlMatching("/contacts/[0-9,]+")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    } 
    
    @Test
    public void retrieveMultipleInValidContact() throws CMCException {
        stubFor(get(urlMatching("/contacts/[0-9,]+"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"8203\",\"message\":\"Contact with the mdn 14102718101,14102718102 could not be found.\"}}")));
        
        List<String> mdns = new ArrayList<String>();
        mdns.add("14102718101");
        mdns.add("14102718102");
        HttpResponseWrapper<ContactsResponse> response = contactApi.retrieveContacts(mdns);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 404);
        assertEquals(response.getResponseBody().getStatus(), "fail");   
        assertEquals(response.getResponseBody().getCode(), "8203");   
        
        List<Contact> contacts = response.getResponseBody().getContactsResponses();
        assertEquals(contacts, null);

        // Verify the request
        List<LoggedRequest> requests = findAll(getRequestedFor(urlMatching("/contacts/[0-9,]+")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }    
    
}
