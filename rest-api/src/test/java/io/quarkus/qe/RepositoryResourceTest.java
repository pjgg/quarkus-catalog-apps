package io.quarkus.qe;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.qe.configuration.Channels;
import io.quarkus.qe.data.RepositoryEntity;
import io.quarkus.qe.model.Repository;
import io.quarkus.qe.model.requests.NewRepositoryRequest;
import io.quarkus.qe.utils.InMemoryKafkaResource;
import io.quarkus.qe.utils.RepositoryEntityUtils;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import io.smallrye.reactive.messaging.connectors.InMemoryConnector;
import io.smallrye.reactive.messaging.connectors.InMemorySink;

@QuarkusTest
@QuarkusTestResource(InMemoryKafkaResource.class)
@QuarkusTestResource(H2DatabaseTestResource.class)
public class RepositoryResourceTest {

    private static final String PATH = "/repository";
    private static final String REPO_URL = "http://github.com/user/repo.git";
    private static final String REPO_URL_1 = "http://github.com/user/repo1.git";
    private static final String REPO_URL_2 = "http://github.com/user/repo2.git";
    private static final long AN_ENTITY_ID = 100;
    private static final int EXPECTED_ALL_REPOS_AMOUNT = 3;


    @Inject
    @Any
    InMemoryConnector connector;

    @Inject
    RepositoryEntityUtils repositoryEntityUtils;

    private Repository repository;
    private RepositoryEntity entity;
    private Response response;
    private InMemorySink<NewRepositoryRequest> responses;

    @BeforeEach
    public void setup() {
        repositoryEntityUtils.deleteAll();
        responses = connector.sink(Channels.NEW_REPOSITORY);
    }

    @Test
    public void shouldAddRepository() {
        givenNewRepositoryRequest(REPO_URL);
        whenAddNewRepository();
        thenResponseIsAccepted();
        thenNewRepositoryRequestIsSent();
    }

    @Test
    public void shouldFailToAddRepositoryWhenRepoIsNull() {
        givenNewRepositoryRequest(null);
        whenAddNewRepository();
        thenResponseIsInvalidRequest();
    }

    @Test
    public void shouldReturnConflictIfRepositoryAlreadyExists() {
        givenExistingRepository(REPO_URL);
        givenNewRepositoryRequest(REPO_URL);
        whenAddNewRepository();
        thenResponseIsConflict();
    }

    @Test
    public void shouldGetRepositoryById() {
        givenExistingRepository(REPO_URL);
        whenGetRepository();
        thenResponseIsOk();
    }

    @Test
    public void shouldGetAllRepository() {
        givenExistingRepository(REPO_URL);
        givenExistingRepository(REPO_URL_1);
        givenExistingRepository(REPO_URL_2);
        whenGetAllRepositories(0, EXPECTED_ALL_REPOS_AMOUNT);
        thenResponseIsOk();
        thenResponseObjectsAmountIs(EXPECTED_ALL_REPOS_AMOUNT);
    }

    @Test
    public void shouldReturnNotFoundWhenEntityDoesNotExist() {
        whenGetRepository();
        thenResponseIsNotFound();
    }

    private void givenExistingRepository(String repoUrl) {
        entity = repositoryEntityUtils.create(repoUrl);
    }

    private void givenNewRepositoryRequest(String repoUrl) {
        repository = new Repository();
        repository.setRepoUrl(repoUrl);
    }

    private void whenAddNewRepository() {
        response = given().contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).and()
                .body(repository).when().post(PATH);
    }

    private void whenGetRepository() {
        long entityId = Optional.ofNullable(entity).map(e -> e.id).orElse(AN_ENTITY_ID);
        response = given().accept(MediaType.APPLICATION_JSON).when().get(PATH + "/" + entityId);
    }

    private void whenGetAllRepositories(int from, int to) {
        var queryParams = String.format("?page=%d&size=%d", from, to);
        response = given().accept(MediaType.APPLICATION_JSON).when().get(PATH + queryParams);
    }

    private void thenNewRepositoryRequestIsSent() {
        assertEquals(1, responses.received().size());
    }

    private void thenResponseIsAccepted() {
        response.then().statusCode(HttpStatus.SC_ACCEPTED);
    }

    private void thenResponseIsOk() {
        response.then().statusCode(HttpStatus.SC_OK);
    }

    private void thenResponseIsInvalidRequest() {
        response.then().statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    private void thenResponseIsNotFound() {
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    private void thenResponseIsConflict() {
        response.then().statusCode(HttpStatus.SC_CONFLICT);
    }

    private void thenResponseObjectsAmountIs(int expectedAmount) {
        assertTrue(response.as(Repository[].class).length == expectedAmount);
    }
}
