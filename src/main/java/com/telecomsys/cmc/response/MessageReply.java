package com.telecomsys.cmc.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data model for message reply.
 */
public class MessageReply {

    /**
     * Message destination. This will be the min of the handset.
     */
    @JsonProperty("from")
    private String min;

    /**
     * message text.
     */
    @JsonProperty("text")
    private String msgText;

    /**
     * The date that this reply was created.
     */
    @JsonProperty("date")
    private String replyDate;

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
     * @return the msgText
     */
    public String getMsgText() {
        return msgText;
    }

    /**
     * @param msgText the msgText to set
     */
    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    /**
     * @return the replyDate
     */
    public String getReplyDate() {
        return replyDate;
    }

    /**
     * @param replyDate the replyDate to set
     */
    public void setReplyDate(String replyDate) {
        this.replyDate = replyDate;
    }

}
