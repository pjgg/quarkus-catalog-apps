package io.quarkus.qe;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

import io.quarkus.qe.exceptions.RepositoryNotFoundException;
import io.quarkus.qe.model.Repository;
import io.quarkus.qe.services.RepositoryService;

@GraphQLApi
public class SearchResource {

    @Inject
    RepositoryService repositoryService;

    @Query("repositories")
    @Description("Get all repositories")
    public List<Repository> getAllRepositories() {
        return repositoryService.findAll();
    }

    @Query("repositoryById")
    @Description("Get repository by ID")
    public Repository getRepositoryByRepoUrl(long id) throws RepositoryNotFoundException {
        return repositoryService.findById(id);
    }

    @Query("repositoryByUrl")
    @Description("Get repository by repository URL")
    public Repository getRepositoryByRepoUrl(String repoUrl) throws RepositoryNotFoundException {
        return repositoryService.findByRepoUrl(repoUrl);
    }

    @Query("repositoriesByExtensions")
    @Description("Get repositories using a list of extensions")
    public List<Repository> getRepositoriesByExtension(List<String> extensions) {
        return repositoryService.findByExtensions(extensions);
    }
}
