package com.telecomsys.cmc;

/**
 * Main class that provides the instance used for communicating with CMC for the different environments.
 */
public final class CmcClients {

    /**
     * Production CMC REST URL.
     */
    public static final String BASE_CMC_URL_PRODUCTION = "https://www.cloudmessagingcenter.com/v1/rest/";

    /**
     * Trial CMC REST URL.
     */
    public static final String BASE_CMC_URL_TRIAL = "https://www.cloudmessagingtrial.com/v1/rest/";

    /**
     * Constructor.
     */
    private CmcClients() {
        // Prevent instantiation
    }

    /**
     * Method to create a CMC client for CMC Trial.
     *
     * @param accountID the REST account identity.
     * @param authenticationToken the authentication token.
     * @return CmcClient instance of the CMC client.
     */
    public static CmcClient trialClient(String accountID, String authenticationToken) {
        return new DefaultCmcClient(BASE_CMC_URL_TRIAL, accountID, authenticationToken);
    }

    /**
     * Method to create a CMC client for CMC Trial.
     *
     * @param accountID the REST account identity.
     * @param authenticationToken the authentication token.
     * @return CmcClient instance of the CMC client.
     */
    public static CmcClient productionClient(String accountID, String authenticationToken) {
        return new DefaultCmcClient(BASE_CMC_URL_PRODUCTION, accountID, authenticationToken);
    }

}
