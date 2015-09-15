package com.telecomsys.cmc.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The group members model. This encapsulates all the elements of any group members request i.e.
 * to add or remove members from a group.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupMembers {

    /**
     * Group members.
     */
    protected List<GroupMember> members;

    /**
     * @return the members
     */
    public List<GroupMember> getMembers() {
        return members;
    }

    /**
     * @param members the members to set
     */
    public void setMembers(List<GroupMember> members) {
        this.members = members;
    }

}
