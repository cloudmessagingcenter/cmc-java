package com.telecomsys.cmc.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Error response.
 */
@JsonRootName(value = "response")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestError {

    /**
     * Status.
     */
    private String status;

    /**
     * Error Code.
     */
    private String code;

    /**
     * Error Message.
     */
    private String message;

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
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
}
