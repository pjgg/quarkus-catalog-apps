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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.quarkus.qe.exceptions.CatalogError;
import io.quarkus.qe.exceptions.RepositoryAlreadyExistsException;
import io.quarkus.qe.exceptions.RepositoryNotFoundException;
import io.quarkus.qe.model.Repository;
import io.quarkus.qe.services.RepositoryService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/repository")
@Transactional
@Tag(name = "Repository", description = "Retrieve and request repository resource")
public class RepositoryResource {

    @Inject
    RepositoryService repositoryService;

    @GET
    @Produces(APPLICATION_JSON)
    @Path("/{id}")

    @Operation(summary = "Retrieve repository by internal ID")
    @APIResponse(name = "GetByID", responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Repository.class, required = true)))
    @APIResponse(responseCode = "404", description = "No found", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CatalogError.class, required = true)))
    public Response get(@Parameter(name = "id", description = "internal id") @PathParam("id") Long id)
            throws RepositoryNotFoundException {
        return Response.ok(repositoryService.findById(id)).build();
    }

    @GET
    @Produces(APPLICATION_JSON)
    @Path("/")
    @Operation(summary = "Retrieve all repositories")
    @APIResponse(name = "RetrieveAll", responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Repository.class, type = SchemaType.ARRAY)))
    @APIResponse(responseCode = "204", description = "No repositories")
    public Response getAll(@QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int size) {
        var allRepositories = repositoryService.findAll(pageIndex, size);
        return allRepositories.isEmpty() ? Response.status(Status.NO_CONTENT).build()
                : Response.ok(allRepositories).build();
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Operation(summary = "Request analyze new repository")
    @APIResponse(name = "NewRepository", responseCode = "202", description = "Accepted")
    @APIResponse(responseCode = "409", description = "Already requested", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CatalogError.class, required = true)))
    public Response add(@Valid Repository request) throws RepositoryAlreadyExistsException {
        repositoryService.sendNewRepositoryRequest(request);
        return Response.accepted().build();
    }

}
