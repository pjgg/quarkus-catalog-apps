package io.quarkus.qe;

import io.quarkus.qe.model.QuarkusExtension;
import java.util.List;

import javax.inject.Inject;

import io.quarkus.qe.exceptions.RepositoryNotFoundException;
import io.quarkus.qe.model.Repository;
import io.quarkus.qe.services.RepositoryService;
import org.eclipse.microprofile.graphql.DefaultValue;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

@GraphQLApi
public class SearchResource {

    @Inject
    RepositoryService repositoryService;

    @Query("repositories")
    @Description("Get all repositories")
    public List<Repository> getAllRepositories(@Name("quarkusVersions") @DefaultValue("[]") List<String> versions) {
        return repositoryService.findAll(versions);
    }

    @Query("repositoryById")
    @Description("Get repository by ID")
    public Repository getRepositoryByRepoUrl(@Name("id") long id) throws RepositoryNotFoundException {
        return repositoryService.findById(id);
    }

    @Query("repositoryByUrl")
    @Description("Get repository by repository URL")
    public Repository getRepositoryByRepoUrl(@Name("repoUrl") String repoUrl) throws RepositoryNotFoundException {
        return repositoryService.findByRepoUrl(repoUrl);
    }

    @Query("repositoriesByExtensions")
    @Description("Get repositories using a list of extensions")
    public List<Repository> getRepositoriesByExtension(@Name("extensions") List<QuarkusExtension> extensions) {
        return repositoryService.findByExtensions(extensions);
    }

    @Query("repositoriesByExtensionsArtifactIds")
    @Description("Get repositories using a list of extensions artifacts ids")
    public List<Repository> getRepositoriesByExtensionArtifactIds(@Name("artifactIds") List<String> artifactIds) {
        return repositoryService.findByExtensionsArtifactIds(artifactIds);
    }
}
