package com.telecomsys.cmc.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data model for message status type (receipt details).
 */
public class MessageStatus {

    /**
     * Message destination. This will be the min of the handset.
     */
    @JsonProperty("to")
    private String min;

    /**
     * Status message text.
     */
    @JsonProperty("deliverystatus")
    private String deliveryStatus;

    /**
     * Message Id.
     */
    private String messageID;

    /**
     * The date that this message was delivered.
     */
    @JsonProperty("deliverydate")
    private String deliveryDate;

    /**
     * @return the min
     */
    public String getMin() {
        return min;
    }

    /**
     * @param min the min to set
     */
    public void setMin(String min) {
        this.min = min;
    }

    /**
     * @return the deliveryStatus
     */
    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    /**
     * @param deliveryStatus the deliveryStatus to set
     */
    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
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
     * @return the deliveryDate
     */
    public String getDeliveryDate() {
        return deliveryDate;
    }

    /**
     * @param deliveryDate the deliveryDate to set
     */
    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}

