package com.telecomsys.cmc.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * The Message replies response model. This encapsulates the response sent when the client requests details about
 * message replies.
 */
@JsonRootName(value = "response")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageRepliesResponse extends RestResponse {

    /**
     * Message replies.
     */
    @JsonProperty("replies")
    private MessageReplies messageReplies;

    /**
     * @return the messageReplies
     */
    public MessageReplies getMessageReplies() {
        return messageReplies;
    }

    /**
     * @param messageReplies the messageReplies to set
     */
    public void setMessageReplies(MessageReplies messageReplies) {
        this.messageReplies = messageReplies;
    }
}
