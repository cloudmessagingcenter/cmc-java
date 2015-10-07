package com.telecomsys.cmc.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The schedule model. This encapsulates elements of the schedule in schedule message request.
 */
public class Schedule {

    /**
     * Message recurrence - once, daily, weekly, monthly.
     */
    private String recurrence;

    /**
     * StartDate.
     */
    @JsonProperty("startdate")
    private String startDate;

    /**
     * EndDate.
     */
    @JsonProperty("enddate")
    private String expireDate;

    /**
     * jobName.
     */
    @JsonProperty("name")
    private String jobName;

    /**
     * @return the recurrence
     */
    public String getRecurrence() {
        return recurrence;
    }

    /**
     * @param recurrence the recurrence to set
     */
    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }

    /**
     * @return the startDate
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the expireDate
     */
    public String getExpireDate() {
        return expireDate;
    }

    /**
     * @param expireDate the expireDate to set
     */
    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    /**
     * @return the jobName
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * @param jobName the jobName to set
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

}
