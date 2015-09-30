package com.telecomsys.cmc.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * The Delivery receipt response model. This encapsulates the response sent when the client requests details about
 * delivery receipt.
 */
@JsonRootName(value = "response")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeliveryReceiptResponse extends RestResponse {

    /**
     * Delivery Receipt.
     */
    private DeliveryReceipt deliveryReceipt;

    /**
     * @return the deliveryReceipt
     */
    public DeliveryReceipt getDeliveryReceipt() {
        return deliveryReceipt;
    }

    /**
     * @param deliveryReceipt the deliveryReceipt to set
     */
    public void setDeliveryReceipt(DeliveryReceipt deliveryReceipt) {
        this.deliveryReceipt = deliveryReceipt;
    }
}
