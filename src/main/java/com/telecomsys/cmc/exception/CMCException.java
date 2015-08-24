package com.telecomsys.cmc.exception;

public class CMCException extends Exception {

    /**
     * Generated serialVersionUID.
     */
    private static final long serialVersionUID = 7934624120603204769L;

    public CMCException(Exception e) {
        super(e);
    }
    
    public CMCException(String msg) {
        super(msg);
    }
    
}