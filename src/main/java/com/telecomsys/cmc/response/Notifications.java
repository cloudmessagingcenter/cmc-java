package com.telecomsys.cmc.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The notification model. This encapsulates a list of destinations and notifications.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Notifications {

    /**
     * list - list of destinations addresses.
     */
    private List<String> to = null;

    /**
     * from address.
     */
    @JsonProperty("from")
    private String fromAddress = null;

    /**
     * list - list of message replies.
     */
    private List<Notification> trackinginformation = null;

    /**
     * Default Constructor.
     */
    public Notifications() {
    }

    /**
     * Constructor.
     *
     * @param notifications a list of  notifications
     */
    public Notifications(List<Notification> notifications) {
        this.trackinginformation = notifications;
    }

    /**
     * @return the to
     */
    public List<String> getTo() {
        return to;
    }

    /**
     * @param to the to to set
     */
    public void setTo(List<String> to) {
        this.to = to;
    }

    /**
     * @return the fromAddress
     */
    public String getFromAddress() {
        return fromAddress;
    }

    /**
     * @param fromAddress the fromAddress to set
     */
    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    /**
     * @return the trackinginformation
     */
    public List<Notification> getTrackinginformation() {
        return trackinginformation;
    }

    /**
     * @param trackinginformation the trackinginformation to set
     */
    public void setTrackinginformation(List<Notification> trackinginformation) {
        this.trackinginformation = trackinginformation;
    }

}
