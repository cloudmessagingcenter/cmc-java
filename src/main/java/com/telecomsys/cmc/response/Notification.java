package com.telecomsys.cmc.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data model for a single notification.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Notification {

    /**
     * Message destination. This will be the min of the handset.
     */
    private String destination;

    /**
     * Status message text.
     */
    @JsonProperty("messagestatus")
    private String messageStatus;

    /**
     * Message Id.
     */
    private String messageID;

    /**
     * Message Text.
     */
    @JsonProperty("messagetext")
    private String messageText;

    /**
     * @return the destination
     */
    public String getDestination() {
        return destination;
    }

    /**
     * @param destination the destination to set
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * @return the messageStatus
     */
    public String getMessageStatus() {
        return messageStatus;
    }

    /**
     * @param messageStatus the messageStatus to set
     */
    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }

    /**
     * @return the messageID
     */
    public String getMessageID() {
        return messageID;
    }

    /**
     * @param messageID the messageID to set
     */
    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    /**
     * @return the messageText
     */
    public String getMessageText() {
        return messageText;
    }

    /**
     * @param messageText the messageText to set
     */
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

}
