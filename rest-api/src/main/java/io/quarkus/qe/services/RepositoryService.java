package io.quarkus.qe.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import io.quarkus.panache.common.Parameters;
import io.quarkus.qe.configuration.Channels;
import io.quarkus.qe.data.RepositoryEntity;
import io.quarkus.qe.data.marshallers.RepositoryMarshaller;
import io.quarkus.qe.exceptions.RepositoryAlreadyExistsException;
import io.quarkus.qe.exceptions.RepositoryNotFoundException;
import io.quarkus.qe.model.Repository;
import io.quarkus.qe.model.requests.NewRepositoryRequest;

@ApplicationScoped
public class RepositoryService {

    @Inject
    @Channel(Channels.NEW_REPOSITORY)
    Emitter<NewRepositoryRequest> newRepositoryRequestEmitter;

    @Channel(Channels.ENRICH_REPOSITORY)
    Emitter<Repository> enrichEmitter;

    @Inject
    RepositoryMarshaller repositoryMarshaller;

    public Repository findById(Long id) throws RepositoryNotFoundException {
        RepositoryEntity entity = RepositoryEntity.findById(id);
        if (entity == null) {
            throw new RepositoryNotFoundException(String.format("Repository ID %d not exist.", id));
        }

        return repositoryMarshaller.fromEntity(entity);
    }

    public List<Repository> findByExtensions(List<String> extensions) {
        return RepositoryEntity.<RepositoryEntity> find(
                "select distinct repo from repository repo inner join fetch repo.extensions ext where ext.name IN :names",
                Parameters.with("names", extensions)).stream()
                .map(repositoryMarshaller::fromEntity)
                .collect(Collectors.toList());
    }

    public Repository findByRepoUrl(String repoUrl) throws RepositoryNotFoundException {
        RepositoryEntity entity = RepositoryEntity.find("repoUrl", repoUrl).singleResult();
        if (entity == null) {
            throw new RepositoryNotFoundException(String.format("Repository with %s not exist.", repoUrl));
        }

        return repositoryMarshaller.fromEntity(entity);
    }

    public List<Repository> findAll() {
        return RepositoryEntity.<RepositoryEntity> findAll()
                .stream()
                .map(repositoryMarshaller::fromEntity)
                .collect(Collectors.toList());
    }

    public List<Repository> findAll(int pageIndex, int size) {
        return RepositoryEntity.<RepositoryEntity> findAll()
                .page(pageIndex, size)
                .stream()
                .map(repositoryMarshaller::fromEntity)
                .collect(Collectors.toList());
    }

    public void sendNewRepositoryRequest(NewRepositoryRequest request) throws RepositoryAlreadyExistsException {
        if (RepositoryEntity.find("repoUrl", request.getRepoUrl()).count() > 0) {
            throw new RepositoryAlreadyExistsException(String.format("Repository %s already exist.", request.getRepoUrl()));
        }
        newRepositoryRequestEmitter.send(request);
    }

    public void updateRepositoryRequest(long id) throws RepositoryNotFoundException {
        enrichEmitter.send(findById(id));
    }
}
