package com.telecomsys.cmc.api;

import java.util.List;

import com.telecomsys.cmc.exception.CMCException;
import com.telecomsys.cmc.http.CmcHttpRequest;
import com.telecomsys.cmc.http.HttpResponseWrapper;
import com.telecomsys.cmc.model.Contact;
import com.telecomsys.cmc.response.ContactsResponse;
import com.telecomsys.cmc.response.RestResponse;
import com.telecomsys.cmc.response.StatusResponse;
import com.telecomsys.cmc.utils.StringUtils;

/**
 * Main facade for managing contacts using the CMC REST API.
 */
public class ContactApi extends CmcBaseApi {

    /**
     * Contacts End point.
     */
    public static final String CONTACTS_URL = "/contacts";

    /**
     * Constructor.
     *
     * @param baseUri base URL to connect to the CMC REST service.
     * @param accountID the REST account identity.
     * @param authenticationToken the authentication token.
     */
    public ContactApi(String baseUri, String accountID, String authenticationToken) {
        super(baseUri, accountID, authenticationToken);
    }

    /**
     * Method to add contact(s) using CMC REST API.
     *
     * @param contacts List of contacts to be added.
     * @return HttpResponseWrapper http response wrapper with the response.
     * @throws CMCException CMC exception if errors.
     */
    public HttpResponseWrapper<StatusResponse> addContacts(List<Contact> contacts) throws CMCException {

        // Create the request with parameters.
        CmcHttpRequest cmcRequest = new CmcHttpRequest(CONTACTS_URL);
        cmcRequest.addBodyParameter("contacts", contacts);
        cmcRequest.setMessageWrapperName("contactList");

        // Send the request to CMC.
        return httpClient.doPost(cmcRequest, StatusResponse.class);
    }

    /**
     * Method to update contact(s) using CMC REST API.
     *
     * @param contacts List of contacts to be updated.
     * @return HttpResponseWrapper http response wrapper with the response.
     * @throws CMCException CMC exception if errors.
     */
    public HttpResponseWrapper<StatusResponse> updateContacts(List<Contact> contacts) throws CMCException {

        // Create the request with parameters.
        CmcHttpRequest cmcRequest = new CmcHttpRequest(CONTACTS_URL);
        cmcRequest.addBodyParameter("contacts", contacts);
        cmcRequest.setMessageWrapperName("contactList");

        // Send the request to CMC.
        return httpClient.doPut(cmcRequest, StatusResponse.class);
    }

    /**
     * Method to delete contact(s) using CMC REST API.
     *
     * @param mdns List of contacts to be deleted identified by their cell numbers.
     * @return HttpResponseWrapper http response wrapper with the response.
     * @throws CMCException CMC exception if errors.
     */
    public HttpResponseWrapper<RestResponse> deleteContacts(List<String> mdns) throws CMCException {

        // Append the matrix parameters
        StringBuilder sb = new StringBuilder();
        sb.append(CONTACTS_URL).append("/");
        String mdnStr = StringUtils.convertStringListToCSV(mdns);
        if (mdnStr != null) {
            sb.append(mdnStr);
        }

        // Create the request with parameters.
        CmcHttpRequest cmcRequest = new CmcHttpRequest(sb.toString());

        // Send the request to CMC.
        return httpClient.doDelete(cmcRequest, RestResponse.class);
    }

    /**
     * Method to delete all the users contacts using CMC REST API.
     *
     * @return HttpResponseWrapper http response wrapper with the response.
     * @throws CMCException CMC exception if errors.
     */
    public HttpResponseWrapper<RestResponse> deleteAllContacts() throws CMCException {

        // Create the request with parameters.
        CmcHttpRequest cmcRequest = new CmcHttpRequest(CONTACTS_URL);
        cmcRequest.addUrlParameter("all", "true");

        // Send the request to CMC.
        return httpClient.doDelete(cmcRequest, RestResponse.class);
    }

    /**
     * Method to retrieve contact(s) using CMC REST API.
     *
     * @param mdns List of contacts to be retrieved identified by their cell numbers.
     * @return HttpResponseWrapper http response wrapper with the response.
     * @throws CMCException CMC exception if errors.
     */
    public HttpResponseWrapper<ContactsResponse> retrieveContacts(List<String> mdns) throws CMCException {

        // Append the matrix parameters
        StringBuilder sb = new StringBuilder();
        sb.append(CONTACTS_URL).append("/");
        String mdnStr = StringUtils.convertStringListToCSV(mdns);
        if (mdnStr != null) {
            sb.append(mdnStr);
        }

        // Create the request with parameters.
        CmcHttpRequest cmcRequest = new CmcHttpRequest(sb.toString());

        // Send the request to CMC.
        return httpClient.doGet(cmcRequest, ContactsResponse.class);
    }

}
