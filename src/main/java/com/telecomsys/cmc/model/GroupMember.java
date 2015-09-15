package com.telecomsys.cmc.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The group member model. This encapsulates all the elements of any group member request.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupMember {

    /**
     * MDN of this group member.
     */
    private String mdn;

    /**
     * Group member name i.e. contact name. Its format is last name-first name.
     */
    @JsonProperty("contact")
    private String contactName;

    /**
     * @return the mdn
     */
    public String getMdn() {
        return mdn;
    }

    /**
     * @param mdn the mdn to set
     */
    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    /**
     * @return the contactName
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * @param contactName the contactName to set
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

}
