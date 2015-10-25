package com.telecomsys.cmc.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * The schedule message model. This encapsulates the request and response sent when the client requests details about a
 * scheduled message.
 */
@JsonRootName("schedulemessage")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleMessage {

    /**
     * Message id of the scheduled message.
     */
    @JsonProperty("messageID")
    private Long messageId;

    /**
     * Message data.
     */
    private Message message;

    /**
     * Schedule data.
     */
    private Schedule schedule;

    /**
     * @return the messageId
     */
    public Long getMessageId() {
        return messageId;
    }

    /**
     * @param messageId the messageId to set
     */
    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    /**
     * @return the schedule
     */
    public Schedule getSchedule() {
        return schedule;
    }

    /**
     * @param schedule the schedule to set
     */
    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    /**
     * @return the message
     */
    public Message getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(Message message) {
        this.message = message;
    }
}
