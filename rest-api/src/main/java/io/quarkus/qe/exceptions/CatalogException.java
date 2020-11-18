package io.quarkus.qe.exceptions;

public class CatalogException extends Exception {

    private final int errorCode;

    public CatalogException(String msg, int errorCode) {
        super(msg);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

}
