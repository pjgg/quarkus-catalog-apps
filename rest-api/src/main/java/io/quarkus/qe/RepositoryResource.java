package io.quarkus.qe;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.quarkus.qe.exceptions.RepositoryAlreadyExistsException;
import io.quarkus.qe.exceptions.RepositoryNotFoundException;
import io.quarkus.qe.model.Repository;
import io.quarkus.qe.services.RepositoryService;

@Path("/repository")
@Transactional
public class RepositoryResource {

    @Inject
    RepositoryService repositoryService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        try {
            return Response.ok(repositoryService.findById(id)).build();
        } catch (RepositoryNotFoundException e) {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response getAll(@QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int size) {
        var allRepositories = repositoryService.findAll(pageIndex, size);
        return allRepositories.isEmpty() ? Response.status(Status.NO_CONTENT).build()
                : Response.ok(allRepositories).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response add(@Valid Repository request) {
        try {
            repositoryService.sendNewRepositoryRequest(request);
            return Response.accepted().build();
        } catch (RepositoryAlreadyExistsException e) {
            return Response.status(Status.CONFLICT).build();
        }

    }

}
