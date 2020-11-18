package io.quarkus.qe.exceptions;

public class UnexpectedCatalogException extends CatalogException {

    private static final long serialVersionUID = 4442033229110468176L;
    public static final int uniqueServiceErrorId = 000;

    public UnexpectedCatalogException(String msg) {
        super(msg, uniqueServiceErrorId);
    }
}
