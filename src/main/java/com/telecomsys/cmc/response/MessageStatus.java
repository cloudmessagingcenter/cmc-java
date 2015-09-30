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
}
