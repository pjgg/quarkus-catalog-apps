package io.quarkus.qe;

import io.quarkus.qe.configuration.Channels;
import io.quarkus.qe.enricher.GitHubEnricher;
import io.quarkus.qe.model.Repository;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class Dispatcher {

    @Inject
    GitHubEnricher gitHubEnricher;

    @Incoming(Channels.ENRICH_REPOSITORY)
    @Outgoing(Channels.UPDATE_REPOSITORY)
    public Repository dispatchRepository(Repository repository) {
        return gitHubEnricher.enrichRepository(repository);
    }
}
