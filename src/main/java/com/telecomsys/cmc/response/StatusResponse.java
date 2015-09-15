package com.telecomsys.cmc.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * CMC REST status response for API calls.
 */
@JsonRootName(value = "response")
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusResponse {

    /**
     * Status responses.
     */
    @JsonProperty("statusList")
    private List<RestResponse> statusResponses;

    /**
     * @return the statusResponses
     */
    public List<RestResponse> getStatusResponses() {
        return statusResponses;
    }

    /**
     * @param statusResponses the statusResponses to set
     */
    public void setStatusResponses(List<RestResponse> statusResponses) {
        this.statusResponses = statusResponses;
    }

}
