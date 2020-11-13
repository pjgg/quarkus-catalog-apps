package io.quarkus.qe.client.github;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/repos")
@RegisterRestClient(configKey = "github-api")
@ApplicationScoped
public interface RepositoryClient {
    @GET
    @Path("/{repositoryPath}")
    @Produces(MediaType.APPLICATION_JSON)
    RepositoryInfo getRepositoryInfo(@PathParam String repositoryPath);
}
