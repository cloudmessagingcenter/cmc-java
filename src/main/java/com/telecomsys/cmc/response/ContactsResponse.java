package com.telecomsys.cmc.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.telecomsys.cmc.model.Contact;

/**
 * CMC REST contacts response for contacts retrieval.
 */
@JsonRootName(value = "response")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactsResponse extends RestResponse {

    /**
     * Status responses.
     */
    @JsonProperty("contactList")
    private List<Contact> contacts;

    /**
     * @return the contacts
     */
    public List<Contact> getContactsResponses() {
        return contacts;
    }

    /**
     * @param contacts the contacts to set
     */
    public void setContactsResponses(List<Contact> contacts) {
        this.contacts = contacts;
    }

}
