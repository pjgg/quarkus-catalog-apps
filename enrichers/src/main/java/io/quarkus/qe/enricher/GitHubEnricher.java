package io.quarkus.qe.enricher;

import io.quarkus.qe.client.github.RepositoryClient;
import io.quarkus.qe.client.github.RepositoryInfo;
import io.quarkus.qe.model.Repository;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class GitHubEnricher {
    @ConfigProperty(name = "enricher.github.baseUrl")
    String baseUrl;

    @Inject
    @RestClient
    RepositoryClient repositoryClient;

    public Repository enrichRepository(Repository repository) {
        final String repoUrl = repository.getRepoUrl();
        final String repositoryPath = StringUtils.removeStart(repoUrl, baseUrl);
        final RepositoryInfo repositoryInfo = repositoryClient.getRepositoryInfo(repositoryPath);
        repository.setSomeUpdate(repositoryInfo.name);
        return repository;
    }
}
