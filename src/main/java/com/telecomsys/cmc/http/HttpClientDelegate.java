package com.telecomsys.cmc.http;

import com.telecomsys.cmc.exception.CMCException;

public interface HttpClientDelegate {   
  
    <T> HttpResponseWrapper<T> doGet(CmcHttpRequest request, Class<T> responseClass) throws CMCException;

    <T> HttpResponseWrapper<T> doPost(CmcHttpRequest request, Class<T> responseClass) throws CMCException;
    
    <T> HttpResponseWrapper<T> doPut(CmcHttpRequest request, Class<T> responseClass) throws CMCException;

    <T> HttpResponseWrapper<T> doDelete(CmcHttpRequest request, Class<T> responseClass) throws CMCException; 

} // HttpClientDelegate