package io.quarkus.qe.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.Status;
import static javax.ws.rs.core.Response.status;

@Provider
public class CatalogExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {

        if (e instanceof RepositoryAlreadyExistsException) {
            RepositoryAlreadyExistsException exp = (RepositoryAlreadyExistsException) e;
            return status(Status.CONFLICT).entity(toCatalogError(exp)).build();
        }

        if (e instanceof RepositoryNotFoundException) {
            RepositoryNotFoundException exp = (RepositoryNotFoundException) e;
            return status(Status.NOT_FOUND).entity(toCatalogError(exp)).build();
        }

        return status(Status.INTERNAL_SERVER_ERROR)
                .entity(toCatalogError(new UnexpectedCatalogException(e.getMessage())))
                .build();
    }

    private CatalogError toCatalogError(CatalogException e) {
        return CatalogError.builder().withCode(e.getErrorCode()).withMsg(e.getMessage()).build();
    }
}
