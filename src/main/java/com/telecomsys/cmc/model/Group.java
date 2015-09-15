package com.telecomsys.cmc.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Group model.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Group {

    /**
     * Group name.
     */
    @JsonProperty("groupname")
    private String name;

    /**
     * Group description.
     */
    @JsonProperty("groupdesc")
    private String description;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
