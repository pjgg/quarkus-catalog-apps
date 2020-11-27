package io.quarkus.qe.consumers;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.enterprise.inject.Any;
import javax.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.qe.configuration.Channels;
import io.quarkus.qe.consumers.utils.InMemoryKafkaResource;
import io.quarkus.qe.data.RepositoryEntity;
import io.quarkus.qe.model.Repository;
import io.quarkus.qe.model.requests.NewRepositoryRequest;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.connectors.InMemoryConnector;
import io.smallrye.reactive.messaging.connectors.InMemorySink;
import io.smallrye.reactive.messaging.connectors.InMemorySource;

@QuarkusTest
@QuarkusTestResource(InMemoryKafkaResource.class)
@QuarkusTestResource(H2DatabaseTestResource.class)
public class NewRepositoryRequestConsumerTest {

    private static final String REPO_URL = "http://github.com/user/repo.git";
    private static final String BRANCH = "master";

    @Inject
    @Any
    InMemoryConnector connector;

    private NewRepositoryRequest repository;
    private InMemorySource<NewRepositoryRequest> requests;
    private InMemorySink<Repository> responses;

    @BeforeEach
    public void setup() {
        requests = connector.source(Channels.NEW_REPOSITORY);
        responses = connector.sink(Channels.ENRICH_REPOSITORY);
    }

    @Test
    public void shouldAddRepositoryAndThenSendToEnrich() {
        givenNewRepositoryWith(REPO_URL);
        whenSendNewRequest();
        thenResponseIsSent();
        thenRepositoryIsStored();
    }

    private void givenNewRepositoryWith(String repoUrl) {
        repository = new NewRepositoryRequest();
        repository.setBranch(BRANCH);
        repository.setRepoUrl(repoUrl);
    }

    private void whenSendNewRequest() {
        requests.send(repository);
    }

    private void thenRepositoryIsStored() {
        List<RepositoryEntity> entities = RepositoryEntity.find("repoUrl", REPO_URL).list();

        assertEquals(1, entities.size());
        assertEquals(BRANCH, entities.get(0).branch);
    }

    private void thenResponseIsSent() {
        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> assertEquals(1, responses.received().size()));
    }

}
