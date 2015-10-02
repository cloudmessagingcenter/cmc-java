package com.telecomsys.cmc.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The message reply model. This encapsulates a list of message replies.
 */
public class MessageReplies {

    /**
     * size - number of  message replies.
     */
    @JsonProperty("numberofreplies")
    private int numberOfReplies = 0;

    /**
     * list of message replies.
     */
    @JsonProperty("replylist")
    private List<MessageReply> replies = null;

    /**
     * @return the numberOfReplies
     */
    public int getNumberOfReplies() {
        return numberOfReplies;
    }

    /**
     * @param numberOfReplies the numberOfReplies to set
     */
    public void setNumberOfReplies(int numberOfReplies) {
        this.numberOfReplies = numberOfReplies;
    }

    /**
     * @return the replies
     */
    public List<MessageReply> getReplies() {
        return replies;
    }

    /**
     * @param replies the replies to set
     */
    public void setReplies(List<MessageReply> replies) {
        this.replies = replies;
    }

}
