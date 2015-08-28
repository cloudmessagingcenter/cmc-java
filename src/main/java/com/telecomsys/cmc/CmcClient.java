package com.telecomsys.cmc;

import com.telecomsys.cmc.exception.CMCException;

/**
 * Main interface used for communication with the CMC REST service.
 */
public interface CmcClient {

    /**
     * Method to send a message using CMC REST API.
     *
     * @param destinations the MIN or groups to send the message to.
     * @param keyword the keyword used to identify the REST connection.
     * @param message the message to be sent.
     * @throws CMCException CMC exception if errors.
     */
    void sendMessage(String destinations, String keyword, String message) throws CMCException;

}
