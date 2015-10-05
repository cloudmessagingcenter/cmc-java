package com.telecomsys.cmc.model;

import java.util.List;

/**
 * Program Reply model.
 */
public class ProgramReply {

    /**
     * Keyword for program.
     */
    private String keyword;

    /**
     * Message destinations.
     */
    private List<String> destinations;

    /**
     * The time in minutes within which replies need to be retrieved.
     */
    private String minutes;

    /**
     * Constructor with mandatory parameters.
     *
    * @param keyword the keyword used to identify the REST connection.
     */
    public ProgramReply(String keyword) {
        this.keyword = keyword;
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
     * @return the minutes
     */
    public String getMinutes() {
        return minutes;
    }

    /**
     * @param minutes the minutes to set
     */
    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

}
