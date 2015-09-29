package com.telecomsys.cmc.model;

import java.util.List;

/**
 * Message model.
 */
public class Message {

    /**
     * Message destinations.
     */
    private List<String> destinations;

    /**
     * Message text.
     */
    private String message;

    /**
     * Keyword for connection.
     */
    private String keyword;

    /**
     * Message subject.
     */
    private String subject;

    /**
     * Notify URL for responses.
     */
    private String notifyURL;

    /**
     * Notify URL for responses.
     */
    private Integer replyExpiry;

    /**
     * Constructor with mandatory parameters.
     *
    * @param destinations the MIN or groups to send the message to.
    * @param keyword the keyword used to identify the REST connection.
    * @param message the message to be sent.
     */
    public Message(List<String> destinations, String keyword, String message) {
        this.message = message;
        this.destinations = destinations;
        this.keyword = keyword;
    }

    /**
     * @return the destinations
     */
    public List<String> getDestinations() {
        return destinations;
    }

    /**
     * @param destinations the destinations to set
     */
    public void setDestinations(List<String> destinations) {
        this.destinations = destinations;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * @param keyword the keyword to set
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the notifyURL
     */
    public String getNotifyURL() {
        return notifyURL;
    }

    /**
     * @param notifyURL the notifyURL to set
     */
    public void setNotifyURL(String notifyURL) {
        this.notifyURL = notifyURL;
    }

    /**
     * @return the replyExpiry
     */
    public Integer getReplyExpiry() {
        return replyExpiry;
    }

    /**
     * @param replyExpiry the replyExpiry to set
     */
    public void setReplyExpiry(Integer replyExpiry) {
        this.replyExpiry = replyExpiry;
    }

}
