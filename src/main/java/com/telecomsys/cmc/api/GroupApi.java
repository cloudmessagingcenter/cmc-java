package com.telecomsys.cmc.api;

import java.util.List;

import com.telecomsys.cmc.exception.CMCException;
import com.telecomsys.cmc.http.CmcHttpRequest;
import com.telecomsys.cmc.http.HttpResponseWrapper;
import com.telecomsys.cmc.model.Group;
import com.telecomsys.cmc.response.GroupResponse;
import com.telecomsys.cmc.response.RestResponse;
import com.telecomsys.cmc.utils.StringUtils;

/**
 * Main facade for managing groups using the CMC REST API.
 */
public class GroupApi extends CmcBaseApi {

    /**
     * Groups End point.
     */
    public static final String GROUPS_URL = "/groups";

    /**
     * Constructor.
     *
     * @param baseUri base URL to connect to the CMC REST service.
     * @param accountID the REST account identity.
     * @param authenticationToken the authentication token.
     */
    public GroupApi(String baseUri, String accountID, String authenticationToken) {
        super(baseUri, accountID, authenticationToken);
    }

    /**
     * Method to add group using CMC REST API.
     *
     * @param group Group to be added.
     * @return HttpResponseWrapper http response wrapper with the response.
     * @throws CMCException CMC exception if errors.
     */
    public HttpResponseWrapper<RestResponse> addGroup(Group group) throws CMCException {

        // Create the request with parameters.
        CmcHttpRequest cmcRequest = new CmcHttpRequest(GROUPS_URL);
        cmcRequest.addBodyParameter("groups", group);

        // Send the request to CMC.
        return httpClient.doPost(cmcRequest, RestResponse.class);
    }

    /**
     * Method to delete group(s) using CMC REST API.
     *
     * @param groupNames List of groups to be deleted identified by their names.
     * @return HttpResponseWrapper http response wrapper with the response.
     * @throws CMCException CMC exception if errors.
     */
    public HttpResponseWrapper<RestResponse> deleteGroups(List<String> groupNames) throws CMCException {

        // Append the matrix parameters
        StringBuilder sb = new StringBuilder();
        sb.append(GROUPS_URL).append("/");
        String groupNamesStr = StringUtils.convertStringListToCSV(groupNames);
        if (groupNamesStr != null) {
            sb.append(groupNamesStr);
        }

        // Create the request with parameters.
        CmcHttpRequest cmcRequest = new CmcHttpRequest(sb.toString());

        // Send the request to CMC.
        return httpClient.doDelete(cmcRequest, RestResponse.class);
    }

    /**
     * Method to delete all the users from groups using CMC REST API.
     *
     * @return HttpResponseWrapper http response wrapper with the response.
     * @throws CMCException CMC exception if errors.
     */
    public HttpResponseWrapper<RestResponse> deleteAllGroups() throws CMCException {

        // Create the request with parameters.
        CmcHttpRequest cmcRequest = new CmcHttpRequest(GROUPS_URL);
        cmcRequest.addUrlParameter("all", "true");

        // Send the request to CMC.
        return httpClient.doDelete(cmcRequest, RestResponse.class);
    }

    /**
     * Method to retrieve group using CMC REST API.
     *
     * @param groupName Group to be retrieved identified by their name.
     * @return HttpResponseWrapper http response wrapper with the response.
     * @throws CMCException CMC exception if errors.
     */
    public HttpResponseWrapper<GroupResponse> retrieveGroup(String groupName) throws CMCException {

        // Append the matrix parameters
        StringBuilder sb = new StringBuilder();
        sb.append(GROUPS_URL).append("/").append(groupName);

        // Create the request with parameters.
        CmcHttpRequest cmcRequest = new CmcHttpRequest(sb.toString());

        // Send the request to CMC.
        return httpClient.doGet(cmcRequest, GroupResponse.class);
    }

}
