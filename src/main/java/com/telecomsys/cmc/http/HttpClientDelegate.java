package com.telecomsys.cmc.http;

import com.telecomsys.cmc.exception.CMCException;

/**
 * Interface class for all HTTP methods implemented for the CMC REST API.
 */
public interface HttpClientDelegate {

    /**
     * Method to handle the HTTP GET for all CMC REST API calls.
     *
     * @param request CMC request.
     * @param responseClass response class for HTTP response.
     * @param <T> This describes the type of response.
     * @return HTTP response wrapper
     * @throws CMCException CMC exception.
     */
    <T> HttpResponseWrapper<T> doGet(CmcHttpRequest request, Class<T> responseClass) throws CMCException;

    /**
     * Method to handle the HTTP POST for all CMC REST API calls.
     *
     * @param request CMC request.
     * @param responseClass response class for HTTP response.
     * @param <T> This describes the type of response.
     * @return HTTP response wrapper
     * @throws CMCException CMC exception.
     */
    <T> HttpResponseWrapper<T> doPost(CmcHttpRequest request, Class<T> responseClass) throws CMCException;

    /**
     * Method to handle the HTTP PUT for all CMC REST API calls.
     *
     * @param request CMC request.
     * @param responseClass response class for HTTP response.
     * @param <T> This describes the type of response.
     * @return HTTP response wrapper
     * @throws CMCException CMC exception.
     */
    <T> HttpResponseWrapper<T> doPut(CmcHttpRequest request, Class<T> responseClass) throws CMCException;

    /**
     * Method to handle the HTTP DELETE for all CMC REST API calls.
     *
     * @param request CMC request.
     * @param responseClass response class for HTTP response.
     * @param <T> This describes the type of response.
     * @return HTTP response wrapper
     * @throws CMCException CMC exception.
     */
    <T> HttpResponseWrapper<T> doDelete(CmcHttpRequest request, Class<T> responseClass) throws CMCException;

}
