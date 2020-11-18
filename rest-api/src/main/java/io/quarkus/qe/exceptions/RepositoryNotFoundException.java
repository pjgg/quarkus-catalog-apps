package io.quarkus.qe.exceptions;

public class RepositoryNotFoundException extends CatalogException {

    private static final long serialVersionUID = -5357444829957725283L;
    public static final int uniqueServiceErrorId = 002;

    public RepositoryNotFoundException(String msg) {
        super(msg, uniqueServiceErrorId);
    }
}
