package io.quarkus.qe.exceptions;

import static javax.ws.rs.core.Response.status;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

@Provider
public class CatalogExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(CatalogExceptionMapper.class);

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

        LOG.error("Unexpected error in service", e);

        return status(Status.INTERNAL_SERVER_ERROR)
                .entity(toCatalogError(new UnexpectedCatalogException(e.getMessage())))
                .build();
    }

    private CatalogError toCatalogError(CatalogException e) {
        return CatalogError.builder().withCode(e.getErrorCode()).withMsg(e.getMessage()).build();
    }
}
