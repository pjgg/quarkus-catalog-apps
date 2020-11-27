package io.quarkus.qe.consumers;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.inject.Any;
import javax.inject.Inject;
import org.gradle.internal.impldep.com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.qe.configuration.Channels;
import io.quarkus.qe.consumers.utils.InMemoryKafkaResource;
import io.quarkus.qe.consumers.utils.RepositoryEntityUtils;
import io.quarkus.qe.data.RepositoryEntity;
import io.quarkus.qe.data.marshallers.LogMarshaller;
import io.quarkus.qe.data.marshallers.RepositoryMarshaller;
import io.quarkus.qe.model.Log;
import io.quarkus.qe.model.QuarkusExtension;
import io.quarkus.qe.model.Repository;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.connectors.InMemoryConnector;
import io.smallrye.reactive.messaging.connectors.InMemorySource;

@QuarkusTest
@QuarkusTestResource(InMemoryKafkaResource.class)
@QuarkusTestResource(H2DatabaseTestResource.class)
public class UpdateRepositoryRequestConsumerTest {

    private static final String REPO_URL = "http://github.com/user/repo.git";
    private static final String NEW_NAME = "The New Repo Name";

    @Inject
    @Any
    InMemoryConnector connector;

    @Inject
    RepositoryEntityUtils repositoryEntityUtils;

    @Inject
    RepositoryMarshaller repositoryMarshaller;

    @Inject
    LogMarshaller logMarshaller;

    private RepositoryEntity entity;
    private Repository repository;
    private InMemorySource<Repository> requests;

    @BeforeEach
    public void setup() {
        requests = connector.source(Channels.UPDATE_REPOSITORY);
        repositoryEntityUtils.deleteAll();
        entity = repositoryEntityUtils.create(REPO_URL);
    }

    @Test
    public void shouldUpdateRepositoryName() {
        givenRepositoryRequestWithName(NEW_NAME);
        whenSendUpdate();
        thenNewNameIsStored();
    }

    @Test
    public void shouldUpdateQuarkusExtensions() {
        givenRepositoryRequestWithExtensions("extension-1", "extension-2");
        whenSendUpdate();
        thenNewExtensionsMatch("extension-1", "extension-2");
    }

    @Test
    public void shouldDeleteOldExtensionsAndUpdateThem() {
        givenRepositoryEntityWithExtensions("extension-1", "extension-2");
        givenRepositoryRequestWithExtensions("extension-2", "extension-3");
        whenSendUpdate();
        thenNewExtensionsMatch("extension-2", "extension-3");
    }

    @Test
    public void shouldUpdateLogs() {
        givenRepositoryRequestWithLogs("one-message", "two-message");
        whenSendUpdate();
        thenNewLogsMatch("one-message", "two-message");
    }

    @Test
    public void shouldDeleteOldLogsAndUpdateThem() {
        givenRepositoryEntityWithLogs("one-message", "two-message");
        givenRepositoryRequestWithLogs("two-message", "three-message");
        whenSendUpdate();
        thenNewLogsMatch("two-message", "three-message");
    }

    private void givenRepositoryEntityWithLogs(String... logs) {
        entity = repositoryEntityUtils.updateLogs(entity.id, Arrays.asList(logs));
    }

    private void givenRepositoryEntityWithExtensions(String... extensions) {
        entity = repositoryEntityUtils.updateExtensions(entity.id, Sets.newHashSet(extensions));
    }

    private void givenRepositoryRequestWithExtensions(String... extensions) {
        repository = repositoryMarshaller.fromEntity(entity);
        repository.getExtensions().clear();
        Stream.of(extensions).map(name -> {
            QuarkusExtension model = new QuarkusExtension();
            model.setName(name);
            return model;
        }).forEach(repository.getExtensions()::add);
    }

    private void givenRepositoryRequestWithLogs(String... logs) {
        repository = repositoryMarshaller.fromEntity(entity);
        repository.getLogs().clear();
        Stream.of(logs).map(Log::info).forEach(repository::addLog);
    }

    private void givenRepositoryRequestWithName(String name) {
        repository = repositoryMarshaller.fromEntity(entity);
        repository.setName(name);
    }

    private void whenSendUpdate() {
        requests.send(repository);
    }

    private void thenNewNameIsStored() {
        thenAssertRepositoryEntity(entity -> assertEquals(NEW_NAME, entity.name));
    }

    private void thenNewExtensionsMatch(String... extensions) {
        Set<String> expectedExtensions = new HashSet<>(Arrays.asList(extensions));
        thenAssertRepositoryEntity(entity -> {
            Set<String> actualExtensions = entity.extensions.stream().map(e -> e.name).collect(Collectors.toSet());

            assertEquals(expectedExtensions.size(), actualExtensions.size());
            assertTrue(expectedExtensions.stream().anyMatch(actualExtensions::contains));
        });
    }

    private void thenNewLogsMatch(String... expectedLogs) {
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            List<String> actualLogs = repositoryEntityUtils.getAllLogs(entity.id);
            assertEquals(expectedLogs.length, actualLogs.size());
            assertTrue(Stream.of(expectedLogs).anyMatch(actualLogs::contains));
        });
    }

    private void thenAssertRepositoryEntity(Consumer<RepositoryEntity> predicate) {
        await().atMost(2, TimeUnit.SECONDS)
                .untilAsserted(() -> predicate.accept(repositoryEntityUtils.findById(repository.getId())));
    }

}
