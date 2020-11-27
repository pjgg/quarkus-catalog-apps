package io.quarkus.qe.consumers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import io.quarkus.qe.configuration.Channels;
import io.quarkus.qe.data.RepositoryEntity;
import io.quarkus.qe.data.marshallers.RepositoryMarshaller;
import io.quarkus.qe.model.Repository;
import io.quarkus.qe.model.requests.NewRepositoryRequest;
import io.smallrye.reactive.messaging.annotations.Blocking;

@ApplicationScoped
public class NewRepositoryRequestConsumer {

    private static final Logger LOG = Logger.getLogger(NewRepositoryRequestConsumer.class);

    @Inject
    @Channel(Channels.ENRICH_REPOSITORY)
    Emitter<Repository> enrichEmitter;

    @Inject
    RepositoryMarshaller repositoryMarshaller;

    @Incoming(Channels.NEW_REPOSITORY)
    @Blocking
    @Transactional
    public void addRepository(NewRepositoryRequest request) {
        try {
            RepositoryEntity entity = new RepositoryEntity();
            entity.repoUrl = request.getRepoUrl();
            entity.branch = request.getBranch();
            entity.persist();
            LOG.info("New repository '" + request.getRepoUrl() + "' with ID " + entity.id);
            enrichEmitter.send(repositoryMarshaller.fromEntity(entity));
        } catch (Exception ex) {
            LOG.warn("The request has been discard. Caused by: " + ex);
        }
    }
}
