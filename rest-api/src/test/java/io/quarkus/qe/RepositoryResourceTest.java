package io.quarkus.qe;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Optional;

import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import io.quarkus.qe.exceptions.CatalogError;
import io.quarkus.qe.exceptions.RepositoryAlreadyExistsException;
import io.quarkus.qe.exceptions.RepositoryNotFoundException;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.qe.model.channels.Channels;
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

    private static final long NOT_FOUND_ENTITY_ID = 777;
    private static final int EXPECTED_ALL_REPOS_AMOUNT = 3;
    private static final String PATH = "/repository";
    private static final String BRANCH = "master";
    private static final String REPO_URL = "http://github.com/user/repo.git";
    private static final String REPO_URL_1 = "http://github.com/user/repo1.git";
    private static final String REPO_URL_2 = "http://github.com/user/repo2.git";
    private static final String EXPECTED_CONFLICT_ERROR_MSG = "Repository " + REPO_URL + " already exist.";
    private static final String EXPECTED_NOT_FOUND_ERROR_MSG = "Repository ID " + NOT_FOUND_ENTITY_ID + " not exist.";

    @Inject
    @Any
    InMemoryConnector connector;

    @Inject
    RepositoryEntityUtils repositoryEntityUtils;

    private NewRepositoryRequest repository;
    private RepositoryEntity entity;
    private Response response;
    private InMemorySink<NewRepositoryRequest> newRepositoryResponses;
    private InMemorySink<Repository> updateRepositoryResponses;

    @BeforeEach
    public void setup() {
        repositoryEntityUtils.deleteAll();
        newRepositoryResponses = connector.sink(Channels.NEW_REPOSITORY);
        updateRepositoryResponses = connector.sink(Channels.ENRICH_REPOSITORY);
    }

    @Test
    public void shouldAddRepository() {
        givenNewRepositoryRequest(REPO_URL);
        whenAddNewRepository();
        thenResponseIsAccepted();
        thenNewRepositoryRequestIsSent();
    }

    @Test
    public void shouldUpdateRepository() throws RepositoryNotFoundException {
        givenExistingRepository(REPO_URL);
        whenUpdateRepository(entity.id);
        thenResponseIsAccepted();
        thenUpdateRepositoryRequestIsSent();
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
        thenResponseErrorCodeIs(RepositoryAlreadyExistsException.uniqueServiceErrorId);
        thenResponseErrorMessageIs(EXPECTED_CONFLICT_ERROR_MSG);
    }

    @Test
    public void shouldReturnRepositoryNotFound() throws RepositoryNotFoundException {
        whenUpdateRepository(NOT_FOUND_ENTITY_ID);
        thenResponseIsNotFound();
        thenResponseErrorCodeIs(RepositoryNotFoundException.uniqueServiceErrorId);
        thenResponseErrorMessageIs(EXPECTED_NOT_FOUND_ERROR_MSG);
    }

    @Test
    public void shouldGetRepositoryById() {
        givenExistingRepository(REPO_URL);
        whenGetRepository(entity.id);
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
        whenGetRepository(NOT_FOUND_ENTITY_ID);
        thenResponseIsNotFound();
        thenResponseErrorCodeIs(RepositoryNotFoundException.uniqueServiceErrorId);
        thenResponseErrorMessageIs(EXPECTED_NOT_FOUND_ERROR_MSG);
    }

    private void givenExistingRepository(String repoUrl) {
        entity = repositoryEntityUtils.create(repoUrl, BRANCH);
    }

    private void givenNewRepositoryRequest(String repoUrl) {
        repository = new NewRepositoryRequest();
        repository.setRepoUrl(repoUrl);
        repository.setBranch(BRANCH);
    }

    private void whenAddNewRepository() {
        response = given().contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).and()
                .body(repository).when().post(PATH);
    }

    private void whenUpdateRepository(Long id) throws RepositoryNotFoundException {
        response = given().contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).and()
                .when().put(PATH + "/" + id);
    }

    private void whenGetRepository(Long defaultEntityId) {
        long entityId = Optional.ofNullable(entity).map(e -> e.id).orElse(defaultEntityId);
        response = given().accept(MediaType.APPLICATION_JSON).when().get(PATH + "/" + entityId);
    }

    private void whenGetAllRepositories(int from, int to) {
        var queryParams = String.format("?page=%d&size=%d", from, to);
        response = given().accept(MediaType.APPLICATION_JSON).when().get(PATH + queryParams);
    }

    private void thenNewRepositoryRequestIsSent() {
        assertEquals(1, newRepositoryResponses.received().size());
    }

    private void thenUpdateRepositoryRequestIsSent() {
        assertEquals(1, updateRepositoryResponses.received().size());
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

    private void thenResponseErrorCodeIs(int expectedCode) {
        assertTrue(response.as(CatalogError.class).getCode() == expectedCode);
    }

    private void thenResponseErrorMessageIs(String expectedMsg) {
        assertEquals(expectedMsg, response.as(CatalogError.class).getMsg());
    }

}
