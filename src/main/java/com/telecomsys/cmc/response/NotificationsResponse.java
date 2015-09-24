package com.telecomsys.cmc.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * The Notifications response model. This encapsulates the response sent when the client requests details about
 * notifications.
 */
@JsonRootName(value = "response")
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationsResponse extends RestResponse {

    /**
     * Message notifications.
     */
    private Notifications notifications;

    /**
     * @return the notifications
     */
    public Notifications getNotifications() {
        return notifications;
    }

    /**
     * @param notifications the notifications to set
     */
    public void setNotifications(Notifications notifications) {
        this.notifications = notifications;
    }
}
