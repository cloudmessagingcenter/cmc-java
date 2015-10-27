package com.telecomsys.cmc.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.telecomsys.cmc.model.Group;

/**
 * CMC REST group response for group retrieval.
 */
@JsonRootName(value = "response")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupResponse extends RestResponse {

    /**
     * Group details.
     */
    private Group group;

    /**
     * @return the group
     */
    public Group getGroup() {
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(Group group) {
        this.group = group;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Group: '").append(group).append("'");
        return sb.toString();
    }
}
