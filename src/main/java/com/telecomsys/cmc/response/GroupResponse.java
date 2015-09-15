package com.telecomsys.cmc.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * CMC REST group response for group retrieval.
 */
@JsonRootName(value = "response")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupResponse extends RestResponse {
}
