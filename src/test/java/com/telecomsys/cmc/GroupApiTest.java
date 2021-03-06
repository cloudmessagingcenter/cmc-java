package com.telecomsys.cmc;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.findAll;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
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
import com.telecomsys.cmc.model.GroupMember;
import com.telecomsys.cmc.model.GroupMembers;
import com.telecomsys.cmc.response.GroupResponse;
import com.telecomsys.cmc.response.RestResponse;

public class GroupApiTest {
    /**
     * CMC REST user name (account ID).
     */
    private static final String USERNAME = "4102951866";

    /**
     * CMC REST user name (account ID).
     */
    private static final String PASSWORD = "Test1234";
    
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
        GroupApi invalidGroupApi = new GroupApi("http://localhost:18089", USERNAME, "1234434343");        
        stubFor(post(urlEqualTo("/groups"))
                .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader("Content-Type", "text/html")
                    .withBody("This request requires HTTP authentication.")));
        
        Group group = new Group();
        group.setName("Test group api");
        group.setDescription("Test group api description");
        invalidGroupApi.addGroup(group);
    }
    
    @Test
    public void addGroupNoMembers() throws CMCException {
        stubFor(post(urlEqualTo("/groups"))
                .willReturn(aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\": {\"status\": \"success\"}}")));
        
        Group group = new Group();
        group.setName("Test rest group api");
        group.setDescription("Test rest group api description");
        HttpResponseWrapper<RestResponse> response = groupApi.addGroup(group);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 201);
        assertEquals(response.getResponseBody().getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlMatching("/groups")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"groups\":{\"groupname\":\"Test rest group api\",\"groupdesc\":\"Test rest group api description\"}}");
    }  
    
    @Test
    public void addInvalidGroupNoMembers() throws CMCException {
        stubFor(post(urlEqualTo("/groups"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"7003\",\"message\":\"Group with name Test rest group api already exists.\"}}")));
        
        Group group = new Group();
        group.setName("Test rest group api");
        group.setDescription("Test rest group api description");
        HttpResponseWrapper<RestResponse> response = groupApi.addGroup(group);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 404);
        assertEquals(response.getResponseBody().getStatus(), "fail");   
        assertEquals(response.getResponseBody().getCode(), "7003"); 

        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlMatching("/groups")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"groups\":{\"groupname\":\"Test rest group api\",\"groupdesc\":\"Test rest group api description\"}}");
    }
    
    @Test
    public void addGroupWithMembers() throws CMCException {
        stubFor(post(urlEqualTo("/groups"))
                .willReturn(aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\": {\"status\": \"success\"}}")));
        
        Group group = new Group();
        group.setName("Test rest group api");
        group.setDescription("Test rest group api description");
        List<GroupMember> members = new ArrayList<GroupMember>();
        GroupMember groupMember = new GroupMember();
        groupMember.setMdn("14102951866");
        members.add(groupMember);
        groupMember = new GroupMember();
        groupMember.setMdn("14102804827");
        members.add(groupMember);
        groupMember = new GroupMember();
        groupMember.setContactName("Wall-John");
        members.add(groupMember);
        group.setMembers(members);
        HttpResponseWrapper<RestResponse> response = groupApi.addGroup(group);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 201);
        assertEquals(response.getResponseBody().getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlMatching("/groups")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"groups\":{\"members\":[{\"mdn\":\"14102951866\"},{\"mdn\":\"14102804827\"},{\"contact\":\"Wall-John\"}],\"groupname\":\"Test rest group api\",\"groupdesc\":\"Test rest group api description\"}}");
    }
    
    @Test
    public void addGroupWithInvalidMembers() throws CMCException {
        stubFor(post(urlEqualTo("/groups"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"7005\",\"message\":\"Group with name Test rest group api was not created as some or all of the contacts do not exist.\"}}")));
        
        Group group = new Group();
        group.setName("Test rest group api");
        group.setDescription("Test rest group api description");
        List<GroupMember> members = new ArrayList<GroupMember>();
        GroupMember groupMember = new GroupMember();
        groupMember.setMdn("14102951866");
        members.add(groupMember);
        groupMember = new GroupMember();
        groupMember.setMdn("14102804827");
        members.add(groupMember);
        groupMember = new GroupMember();
        groupMember.setContactName("Doe-John");
        members.add(groupMember);
        group.setMembers(members);
        HttpResponseWrapper<RestResponse> response = groupApi.addGroup(group);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 404);
        assertEquals(response.getResponseBody().getStatus(), "fail");   
        assertEquals(response.getResponseBody().getCode(), "7005"); 

        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlMatching("/groups")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"groups\":{\"members\":[{\"mdn\":\"14102951866\"},{\"mdn\":\"14102804827\"},{\"contact\":\"Doe-John\"}],\"groupname\":\"Test rest group api\",\"groupdesc\":\"Test rest group api description\"}}");
    } 
    
    @Test
    public void addGroupSingleMdnMember() throws CMCException {
        stubFor(post(urlEqualTo("/groups"))
                .willReturn(aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\": {\"status\": \"success\"}}")));
        
        Group group = new Group();
        group.setName("Test rest group api");
        group.setDescription("Test rest group api description");
        List<GroupMember> members = new ArrayList<GroupMember>();
        GroupMember groupMember = new GroupMember();
        groupMember.setMdn("14102951866");
        members.add(groupMember);
        group.setMembers(members);
        HttpResponseWrapper<RestResponse> response = groupApi.addGroup(group);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 201);
        assertEquals(response.getResponseBody().getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlMatching("/groups")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"groups\":{\"members\":[{\"mdn\":\"14102951866\"}],\"groupname\":\"Test rest group api\",\"groupdesc\":\"Test rest group api description\"}}");
    }
    
    @Test
    public void addGroupMultipleMdnMembers() throws CMCException {
        stubFor(post(urlEqualTo("/groups"))
                .willReturn(aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\": {\"status\": \"success\"}}")));
        
        Group group = new Group();
        group.setName("Test rest group api");
        group.setDescription("Test rest group api description");
        List<GroupMember> members = new ArrayList<GroupMember>();
        GroupMember groupMember = new GroupMember();
        groupMember.setMdn("14102951866");
        members.add(groupMember);
        groupMember = new GroupMember();
        groupMember.setMdn("14102804827");
        members.add(groupMember);
        groupMember = new GroupMember();
        groupMember.setMdn("14102951927");
        members.add(groupMember);
        group.setMembers(members);
        HttpResponseWrapper<RestResponse> response = groupApi.addGroup(group);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 201);
        assertEquals(response.getResponseBody().getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlMatching("/groups")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"groups\":{\"members\":[{\"mdn\":\"14102951866\"},{\"mdn\":\"14102804827\"},{\"mdn\":\"14102951927\"}],\"groupname\":\"Test rest group api\",\"groupdesc\":\"Test rest group api description\"}}");
    }
    
    @Test
    public void addGroupSingleContactMember() throws CMCException {
        stubFor(post(urlEqualTo("/groups"))
                .willReturn(aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\": {\"status\": \"success\"}}")));
        
        Group group = new Group();
        group.setName("Test rest group api");
        group.setDescription("Test rest group api description");
        List<GroupMember> members = new ArrayList<GroupMember>();
        GroupMember groupMember = new GroupMember();
        groupMember.setContactName("Doe-John");
        members.add(groupMember);
        group.setMembers(members);
        HttpResponseWrapper<RestResponse> response = groupApi.addGroup(group);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 201);
        assertEquals(response.getResponseBody().getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlMatching("/groups")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"groups\":{\"members\":[{\"contact\":\"Doe-John\"}],\"groupname\":\"Test rest group api\",\"groupdesc\":\"Test rest group api description\"}}");
    }
    
    @Test
    public void addGroupInvalidSingleContactMember() throws CMCException {
        stubFor(post(urlEqualTo("/groups"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"7005\",\"message\":\"Group with name Test rest group api was not created as some or all of the contacts do not exist.\"}}")));
        
        Group group = new Group();
        group.setName("Test rest group api");
        group.setDescription("Test rest group api description");
        List<GroupMember> members = new ArrayList<GroupMember>();
        GroupMember groupMember = new GroupMember();
        groupMember.setContactName("Doe-John");
        members.add(groupMember);
        group.setMembers(members);
        HttpResponseWrapper<RestResponse> response = groupApi.addGroup(group);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 404);
        assertEquals(response.getResponseBody().getStatus(), "fail");   
        assertEquals(response.getResponseBody().getCode(), "7005"); 

        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlMatching("/groups")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"groups\":{\"members\":[{\"contact\":\"Doe-John\"}],\"groupname\":\"Test rest group api\",\"groupdesc\":\"Test rest group api description\"}}");
    }
    
    @Test
    public void addGroupMultipleContactMembers() throws CMCException {
        stubFor(post(urlEqualTo("/groups"))
                .willReturn(aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\": {\"status\": \"success\"}}")));
        
        Group group = new Group();
        group.setName("Test rest group api");
        group.setDescription("Test rest group api description");
        List<GroupMember> members = new ArrayList<GroupMember>();
        GroupMember groupMember = new GroupMember();
        groupMember.setContactName("Doe-John");
        members.add(groupMember);
        groupMember = new GroupMember();
        groupMember.setContactName("Wall-John");
        members.add(groupMember);
        group.setMembers(members);
        HttpResponseWrapper<RestResponse> response = groupApi.addGroup(group);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 201);
        assertEquals(response.getResponseBody().getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlMatching("/groups")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"groups\":{\"members\":[{\"contact\":\"Doe-John\"},{\"contact\":\"Wall-John\"}],\"groupname\":\"Test rest group api\",\"groupdesc\":\"Test rest group api description\"}}");
    }
    
    @Test
    public void addGroupInvalidMultipleContactMembers() throws CMCException {
        stubFor(post(urlEqualTo("/groups"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"7005\",\"message\":\"Group with name Test rest group api was not created as some or all of the contacts do not exist.\"}}")));
        
        Group group = new Group();
        group.setName("Test rest group api");
        group.setDescription("Test rest group api description");
        List<GroupMember> members = new ArrayList<GroupMember>();
        GroupMember groupMember = new GroupMember();
        groupMember.setContactName("Doe-John");
        members.add(groupMember);
        groupMember = new GroupMember();
        groupMember.setContactName("Wall-John");
        members.add(groupMember);
        group.setMembers(members);
        HttpResponseWrapper<RestResponse> response = groupApi.addGroup(group);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 404);
        assertEquals(response.getResponseBody().getStatus(), "fail");   
        assertEquals(response.getResponseBody().getCode(), "7005"); 

        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlMatching("/groups")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"groups\":{\"members\":[{\"contact\":\"Doe-John\"},{\"contact\":\"Wall-John\"}],\"groupname\":\"Test rest group api\",\"groupdesc\":\"Test rest group api description\"}}");
    }
    
    @Test
    public void addGroupMemberSingleMdn() throws CMCException {
        stubFor(post(urlPathMatching("/groups/(.*)/members"))
                .willReturn(aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\": {\"status\": \"success\"}}")));
        
        String groupName = "Test rest group api";
        GroupMembers groupMembers = new GroupMembers();
        List<GroupMember> members = new ArrayList<GroupMember>();
        GroupMember groupMember = new GroupMember();
        groupMember.setMdn("14102951866");
        members.add(groupMember);
        groupMembers.setMembers(members);
        HttpResponseWrapper<RestResponse> response = groupApi.addGroupMember(groupMembers, groupName);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 201);
        assertEquals(response.getResponseBody().getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlPathMatching("/groups/(.*)/members")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"groupmembers\":{\"members\":[{\"mdn\":\"14102951866\"}]}}");
    }
    
    @Test
    public void addGroupMemberInvalidSingleMdn() throws CMCException {
        stubFor(post(urlPathMatching("/groups/(.*)/members"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"7305\",\"message\":\"Group member already exists for the group TEST REST GROUP API.\"}}")));
        
        String groupName = "Test rest group api";
        GroupMembers groupMembers = new GroupMembers();
        List<GroupMember> members = new ArrayList<GroupMember>();
        GroupMember groupMember = new GroupMember();
        groupMember.setMdn("14102951866");
        members.add(groupMember);
        groupMembers.setMembers(members);
        HttpResponseWrapper<RestResponse> response = groupApi.addGroupMember(groupMembers, groupName);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 404);
        assertEquals(response.getResponseBody().getStatus(), "fail");   
        assertEquals(response.getResponseBody().getCode(), "7305"); 

        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlPathMatching("/groups/(.*)/members")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"groupmembers\":{\"members\":[{\"mdn\":\"14102951866\"}]}}");
    }
    
    @Test
    public void addGroupMemberMultipleMdns() throws CMCException {
        stubFor(post(urlPathMatching("/groups/(.*)/members"))
                .willReturn(aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\": {\"status\": \"success\"}}")));
        
        String groupName = "Test rest group api";
        GroupMembers groupMembers = new GroupMembers();
        List<GroupMember> members = new ArrayList<GroupMember>();
        GroupMember groupMember = new GroupMember();
        groupMember.setMdn("14102951866");
        members.add(groupMember);
        
        groupMember = new GroupMember();
        groupMember.setMdn("14102804827");
        members.add(groupMember);
        
        groupMember = new GroupMember();
        groupMember.setMdn("14102951927");
        members.add(groupMember);
        
        groupMembers.setMembers(members);
        HttpResponseWrapper<RestResponse> response = groupApi.addGroupMember(groupMembers, groupName);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 201);
        assertEquals(response.getResponseBody().getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlPathMatching("/groups/(.*)/members")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"groupmembers\":{\"members\":[{\"mdn\":\"14102951866\"},{\"mdn\":\"14102804827\"},{\"mdn\":\"14102951927\"}]}}");
    }
    
    @Test
    public void addGroupMemberInvalidMultipleMdns() throws CMCException {
        stubFor(post(urlPathMatching("/groups/(.*)/members"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"7305\",\"message\":\"Group member already exists for the group TEST REST GROUP API.\"}}")));
        
        String groupName = "Test rest group api";
        GroupMembers groupMembers = new GroupMembers();
        List<GroupMember> members = new ArrayList<GroupMember>();
        GroupMember groupMember = new GroupMember();
        groupMember.setMdn("14102951866");
        members.add(groupMember);
        
        groupMember = new GroupMember();
        groupMember.setMdn("14102804827");
        members.add(groupMember);
        
        groupMember = new GroupMember();
        groupMember.setMdn("14102951927");
        members.add(groupMember);
        
        groupMembers.setMembers(members);
        HttpResponseWrapper<RestResponse> response = groupApi.addGroupMember(groupMembers, groupName);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 404);
        assertEquals(response.getResponseBody().getStatus(), "fail");   
        assertEquals(response.getResponseBody().getCode(), "7305"); 

        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlPathMatching("/groups/(.*)/members")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"groupmembers\":{\"members\":[{\"mdn\":\"14102951866\"},{\"mdn\":\"14102804827\"},{\"mdn\":\"14102951927\"}]}}");
    }
    
    @Test
    public void addGroupMemberSingleContact() throws CMCException {
        stubFor(post(urlPathMatching("/groups/(.*)/members"))
                .willReturn(aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\": {\"status\": \"success\"}}")));
        
        String groupName = "Test rest group api";
        GroupMembers groupMembers = new GroupMembers();
        List<GroupMember> members = new ArrayList<GroupMember>();
        GroupMember groupMember = new GroupMember();
        groupMember.setContactName("Doe-John");
        members.add(groupMember);
        groupMembers.setMembers(members);
        HttpResponseWrapper<RestResponse> response = groupApi.addGroupMember(groupMembers, groupName);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 201);
        assertEquals(response.getResponseBody().getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlPathMatching("/groups/(.*)/members")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"groupmembers\":{\"members\":[{\"contact\":\"Doe-John\"}]}}");
    }
    
    @Test
    public void addGroupMemberInvalidSingleContact() throws CMCException {
        stubFor(post(urlPathMatching("/groups/(.*)/members"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"7304\",\"message\":\"Group members for group with name TEST REST GROUP API was not updated as some or all of the contacts do not exist.\"}}")));
        
        String groupName = "Test rest group api";
        GroupMembers groupMembers = new GroupMembers();
        List<GroupMember> members = new ArrayList<GroupMember>();
        GroupMember groupMember = new GroupMember();
        groupMember.setContactName("Doe-John");
        members.add(groupMember);
        groupMembers.setMembers(members);
        HttpResponseWrapper<RestResponse> response = groupApi.addGroupMember(groupMembers, groupName);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 404);
        assertEquals(response.getResponseBody().getStatus(), "fail");   
        assertEquals(response.getResponseBody().getCode(), "7304"); 

        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlPathMatching("/groups/(.*)/members")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"groupmembers\":{\"members\":[{\"contact\":\"Doe-John\"}]}}");
    }
    
    @Test
    public void addGroupMemberMultipleContacts() throws CMCException {
        stubFor(post(urlPathMatching("/groups/(.*)/members"))
                .willReturn(aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\": {\"status\": \"success\"}}")));
        
        String groupName = "Test rest group api";
        GroupMembers groupMembers = new GroupMembers();
        List<GroupMember> members = new ArrayList<GroupMember>();
        GroupMember groupMember = new GroupMember();
        groupMember.setContactName("Doe-John");
        members.add(groupMember);
        groupMember = new GroupMember();
        groupMember.setContactName("Wall-John");
        members.add(groupMember);
        groupMembers.setMembers(members);
        HttpResponseWrapper<RestResponse> response = groupApi.addGroupMember(groupMembers, groupName);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 201);
        assertEquals(response.getResponseBody().getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlPathMatching("/groups/(.*)/members")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"groupmembers\":{\"members\":[{\"contact\":\"Doe-John\"},{\"contact\":\"Wall-John\"}]}}");
    }
    
    @Test
    public void addGroupMemberInvalidMultipleContacts() throws CMCException {
        stubFor(post(urlPathMatching("/groups/(.*)/members"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"7304\",\"message\":\"Group members for group with name TEST REST GROUP API was not updated as some or all of the contacts do not exist.\"}}")));
        
        String groupName = "Test rest group api";
        GroupMembers groupMembers = new GroupMembers();
        List<GroupMember> members = new ArrayList<GroupMember>();
        GroupMember groupMember = new GroupMember();
        groupMember.setContactName("Doe-John");
        members.add(groupMember);
        groupMember = new GroupMember();
        groupMember.setContactName("Wall-John");
        members.add(groupMember);
        groupMembers.setMembers(members);
        HttpResponseWrapper<RestResponse> response = groupApi.addGroupMember(groupMembers, groupName);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 404);
        assertEquals(response.getResponseBody().getStatus(), "fail");   
        assertEquals(response.getResponseBody().getCode(), "7304"); 

        // Verify the request
        List<LoggedRequest> requests = findAll(postRequestedFor(urlPathMatching("/groups/(.*)/members")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "{\"groupmembers\":{\"members\":[{\"contact\":\"Doe-John\"},{\"contact\":\"Wall-John\"}]}}");
    }
    
    @Test
    public void deleteGroupSingleMdnMember() throws CMCException {
        stubFor(delete(urlMatching("/groups/(.*)/members/(.*)"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\": {\"status\": \"success\"}}")));
        
        String groupName = "Test rest group api";
        String members = "14102951866";
        HttpResponseWrapper<RestResponse> response = groupApi.deleteGroupMember(groupName, members);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(deleteRequestedFor(urlMatching("/groups/(.*)/members/(.*)")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }
    
    @Test
    public void deleteGroupMultipleMdnMembers() throws CMCException {
        stubFor(delete(urlMatching("/groups/(.*)/members/(.*)"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\": {\"status\": \"success\"}}")));
        
        String groupName = "Test rest group api";
        StringBuffer members = new StringBuffer();
        members.append("14102951866");
        members.append(",");
        members.append("14102951866");
        HttpResponseWrapper<RestResponse> response = groupApi.deleteGroupMember(groupName, members.toString());
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(deleteRequestedFor(urlMatching("/groups/(.*)/members/(.*)")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }
    
    @Test
    public void deleteGroupSingleContactMember() throws CMCException {
        stubFor(delete(urlMatching("/groups/(.*)/members/(.*)"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\": {\"status\": \"success\"}}")));
        
        String groupName = "Test rest group api";
        String members = "Doe-John";
        HttpResponseWrapper<RestResponse> response = groupApi.deleteGroupMember(groupName, members);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(deleteRequestedFor(urlMatching("/groups/(.*)/members/(.*)")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }
    
    @Test
    public void deleteGroupInvalidSingleContactMember() throws CMCException {
        stubFor(delete(urlMatching("/groups/(.*)/members/(.*)"))
                .willReturn(aResponse()
                .withStatus(404)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"7304\",\"message\":\"Group members for group with name TEST REST GROUP API was not updated as some or all of the contacts do not exist.\"}}")));
            
        String groupName = "Test rest group api";
        String members = "Doe-John";
        HttpResponseWrapper<RestResponse> response = groupApi.deleteGroupMember(groupName, members);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 404);
        assertEquals(response.getResponseBody().getCode(), "7304");
        assertEquals(response.getResponseBody().getStatus(), "fail");   

        // Verify the request
        List<LoggedRequest> requests = findAll(deleteRequestedFor(urlMatching("/groups/(.*)/members/(.*)")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }
    
    @Test
    public void deleteGroupMultipleContactMembers() throws CMCException {
        stubFor(delete(urlMatching("/groups/(.*)/members/(.*)"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\": {\"status\": \"success\"}}")));
        
        String groupName = "Test rest group api";
        StringBuffer members = new StringBuffer();
        members.append("Doe-John");
        members.append(",");
        members.append("Wall-John");
        HttpResponseWrapper<RestResponse> response = groupApi.deleteGroupMember(groupName, members.toString());
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(deleteRequestedFor(urlMatching("/groups/(.*)/members/(.*)")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }
    
    @Test
    public void deleteGroupMembers() throws CMCException {
        stubFor(delete(urlMatching("/groups/(.*)/members/(.*)"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\": {\"status\": \"success\"}}")));
        
        String groupName = "Test rest group api";
        StringBuffer members = new StringBuffer();
        members.append("14102951866");
        members.append(",");
        members.append("14102951866");
        members.append(",");
        members.append("Doe-John");
        members.append(",");
        members.append("Wall-John");
        HttpResponseWrapper<RestResponse> response = groupApi.deleteGroupMember(groupName, members.toString());
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(deleteRequestedFor(urlMatching("/groups/(.*)/members/(.*)")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }
    
    @Test
    public void deleteInvalidGroupMembers() throws CMCException {
        stubFor(delete(urlMatching("/groups/(.*)/members/(.*)"))
                .willReturn(aResponse()
                .withStatus(404)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"7304\",\"message\":\"Group members for group with name TEST REST GROUP API was not updated as some or all of the contacts do not exist.\"}}")));
            
        String groupName = "Test rest group api";
        StringBuffer members = new StringBuffer();
        members.append("14102951866");
        members.append(",");
        members.append("14102951866");
        members.append(",");
        members.append("Doe-John");
        members.append(",");
        members.append("Wall-John");
        HttpResponseWrapper<RestResponse> response = groupApi.deleteGroupMember(groupName, members.toString());
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 404);
        assertEquals(response.getResponseBody().getCode(), "7304");
        assertEquals(response.getResponseBody().getStatus(), "fail");   

        // Verify the request
        List<LoggedRequest> requests = findAll(deleteRequestedFor(urlMatching("/groups/(.*)/members/(.*)")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }
    
    @Test
    public void deleteSingleValidGroup() throws CMCException {
        stubFor(delete(urlMatching("/groups/(.*)"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\": {\"status\": \"success\"}}")));
        
        List<String> groupNames = new ArrayList<String>();
        groupNames.add("Test rest group api");
        HttpResponseWrapper<RestResponse> response = groupApi.deleteGroups(groupNames);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(deleteRequestedFor(urlMatching("/groups/(.*)")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }
    
    @Test
    public void deleteSingleInValidGroup() throws CMCException {
        stubFor(delete(urlMatching("/groups/(.*)"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"7103\",\"message\":\"Some or all of the groups with names Test rest group api not found.\"}}")));
        
        List<String> groupNames = new ArrayList<String>();
        groupNames.add("Test rest group api");
        HttpResponseWrapper<RestResponse> response = groupApi.deleteGroups(groupNames);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 404);
        assertEquals(response.getResponseBody().getCode(), "7103");
        assertEquals(response.getResponseBody().getStatus(), "fail");   

        // Verify the request
        List<LoggedRequest> requests = findAll(deleteRequestedFor(urlMatching("/groups/(.*)")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }    
    
    @Test
    public void deleteMultipleValidGroup() throws CMCException {
        stubFor(delete(urlMatching("/groups/(.*)"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\": {\"status\": \"success\"}}")));
        
        List<String> groupNames = new ArrayList<String>();
        groupNames.add("Test rest group api");
        groupNames.add("Test group api");
        HttpResponseWrapper<RestResponse> response = groupApi.deleteGroups(groupNames);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(deleteRequestedFor(urlMatching("/groups/(.*)")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }   
    
    @Test
    public void deleteMultipleInValidGroup() throws CMCException {
        stubFor(delete(urlMatching("/groups/(.*)"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"7103\",\"message\":\"Some or all of the groups with names Test rest group api, Test group api not found.\"}}")));
        
        List<String> groupNames = new ArrayList<String>();
        groupNames.add("Test rest group api");
        groupNames.add("Test group api");
        HttpResponseWrapper<RestResponse> response = groupApi.deleteGroups(groupNames);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 404);
        assertEquals(response.getResponseBody().getStatus(), "fail");   
        assertEquals(response.getResponseBody().getCode(), "7103");

        // Verify the request
        List<LoggedRequest> requests = findAll(deleteRequestedFor(urlMatching("/groups/(.*)")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }  
    
    @Test
    public void deleteAllGroups() throws CMCException {
        stubFor(delete(urlPathMatching("/groups*"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\": {\"status\": \"success\"}}")));
        
        HttpResponseWrapper<RestResponse> response = groupApi.deleteAllGroups();
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        assertEquals(response.getResponseBody().getStatus(), "success");   

        // Verify the request
        List<LoggedRequest> requests = findAll(deleteRequestedFor(urlPathMatching("/groups*")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }    
    
    @Test
    public void retrieveSingleValidGroup() throws CMCException {
        stubFor(get(urlMatching("/groups/(.*)"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"group\":{\"groupname\":\"Test rest group api\",\"groupdesc\":\"Test rest group api description\"}}}")));
        
        String groupName = "Test rest group api";
        HttpResponseWrapper<GroupResponse> response = groupApi.retrieveGroup(groupName);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 200);
        
        Group group = response.getResponseBody().getGroup();
        assertEquals(group.getName(), "Test rest group api");
        assertEquals(group.getDescription(), "Test rest group api description");

        // Verify the request
        List<LoggedRequest> requests = findAll(getRequestedFor(urlMatching("/groups/(.*)")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    } 
    
    @Test
    public void retrieveSingleInValidGroup() throws CMCException {
        stubFor(get(urlMatching("/groups/(.*)"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"response\":{\"status\":\"fail\",\"code\":\"7203\",\"message\":\"Group with the name Test rest group api could not be found.\"}}")));
        
        String groupName = "Test rest group api";
        HttpResponseWrapper<GroupResponse> response = groupApi.retrieveGroup(groupName);
        
        // Verify the response.
        assertEquals(response.getHttpStatusCode(), 404);
        assertEquals(response.getResponseBody().getStatus(), "fail");   
        assertEquals(response.getResponseBody().getCode(), "7203");   
        
        Group group = response.getResponseBody().getGroup();
        assertEquals(group, null);

        // Verify the request
        List<LoggedRequest> requests = findAll(getRequestedFor(urlMatching("/groups/(.*)")));
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getBodyAsString(), "");
    }   
    
}