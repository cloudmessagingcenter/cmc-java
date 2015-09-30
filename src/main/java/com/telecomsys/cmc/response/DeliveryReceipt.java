package com.telecomsys.cmc.response;

import java.util.List;

/**
 * The receipt response model. This encapsulates a list of message statuses.
 */
public class DeliveryReceipt {

    /**
     * list - list of message status.
     */
    private List<MessageStatus> deliverystatuslist = null;

    /**
     * Default Constructor.
     */
    public DeliveryReceipt() {
    }

    /**
     * Constructor.
     *
     * @param list  list of  message status
     */
    public DeliveryReceipt(List<MessageStatus> list) {
        this.deliverystatuslist = list;
    }

    /**
     * @param list the list of message status
     */
    public void setDeliverystatuslist(List<MessageStatus> list) {
        this.deliverystatuslist = list;
    }

    /**
     * get the list of message statuses.
     * @return deliverystatuslist
     */
    public List<MessageStatus> getDeliverystatuslist() {
       return this.deliverystatuslist;
    }

}
