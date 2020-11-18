package io.quarkus.qe.exceptions;

public class RepositoryAlreadyExistsException extends CatalogException {

    private static final long serialVersionUID = 8119616657480723651L;
    public static final int uniqueServiceErrorId = 001;

    public RepositoryAlreadyExistsException(String msg) {
        super(msg, uniqueServiceErrorId);
    }

}
