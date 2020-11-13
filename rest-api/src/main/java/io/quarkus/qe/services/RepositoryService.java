package io.quarkus.qe.services;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import io.quarkus.qe.configuration.Channels;
import io.quarkus.qe.data.RepositoryEntity;
import io.quarkus.qe.data.marshallers.RepositoryMarshaller;
import io.quarkus.qe.exceptions.RepositoryAlreadyExistsException;
import io.quarkus.qe.exceptions.RepositoryNotFoundException;
import io.quarkus.qe.model.Repository;
import io.quarkus.qe.model.requests.NewRepositoryRequest;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class RepositoryService {

    @Inject
    @Channel(Channels.NEW_REPOSITORY)
    Emitter<NewRepositoryRequest> newRepositoryRequestEmitter;

    @Inject
    RepositoryMarshaller repositoryMarshaller;

    public Repository findById(Long id) throws RepositoryNotFoundException {
        RepositoryEntity entity = RepositoryEntity.findById(id);
        if (entity == null) {
            throw new RepositoryNotFoundException();
        }

        return repositoryMarshaller.fromEntity(entity);
    }

    public List<Repository> findAll(final int pageIndex, final int size) {
        return RepositoryEntity.findAll()
                .page(pageIndex, size)
                .stream()
                .map(entity -> repositoryMarshaller.fromEntity((RepositoryEntity)entity))
                .collect(Collectors.toList());
    }

    public void sendNewRepositoryRequest(Repository request) throws RepositoryAlreadyExistsException {
        if (RepositoryEntity.find("repoUrl", request.getRepoUrl()).count() > 0) {
            throw new RepositoryAlreadyExistsException();
        }

        newRepositoryRequestEmitter.send(new NewRepositoryRequest(request.getRepoUrl()));
    }
}
