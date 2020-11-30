package io.quarkus.qe;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.json.Json;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Sets;

import io.quarkus.qe.data.RepositoryEntity;
import io.quarkus.qe.utils.InMemoryKafkaResource;
import io.quarkus.qe.utils.RepositoryEntityUtils;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

@QuarkusTest
@QuarkusTestResource(InMemoryKafkaResource.class)
@QuarkusTestResource(H2DatabaseTestResource.class)
public class SearchResourceTest {

    private static final String PATH = "/graphql";
    private static final String REPO_URL = "http://github.com/user/repo.git";
    private static final String BRANCH = "master";

    @Inject
    RepositoryEntityUtils repositoryEntityUtils;

    private RepositoryEntity entity;
    private ValidatableResponse response;

    @BeforeEach
    public void setup() {
        repositoryEntityUtils.deleteAll();
    }

    @Test
    public void testRepositoriesQuery() {
        givenExistingRepository();
        whenRunGraphqlRepositoriesQuery();
        thenRepositoryIsFound();
    }

    @Test
    public void testRepositoryByIdQuery() {
        givenExistingRepository();
        whenRunGraphqlRepositoryByIdQuery();
        thenRepositoryIsFound();
    }

    @Test
    public void testRepositoryByUrlQuery() {
        givenExistingRepository();
        whenRunGraphqlRepositoryByUrlQuery();
        thenRepositoryIsFound();
    }

    @Test
    public void testRepositoriesByExtensionsQuery() {
        givenExistingRepositoryWithExtensions("quarkus-a", "quarkus-b");
        whenRunGraphqlRepositoriesByExtensionsQuery("quarkus-b");
        thenRepositoryIsFound();
    }

    @Test
    public void testRepositoriesByExtensionsQueryShouldReturnEmpty() {
        givenExistingRepositoryWithExtensions("quarkus-a", "quarkus-b");
        whenRunGraphqlRepositoriesByExtensionsQuery("quarkus-notfound");
        thenRepositoryIsNotFound();
    }

    private void givenExistingRepository() {
        entity = repositoryEntityUtils.create(REPO_URL, BRANCH);
    }

    private void givenExistingRepositoryWithExtensions(String... extensions) {
        givenExistingRepository();
        repositoryEntityUtils.updateExtensions(entity.id, Sets.newHashSet(extensions));
    }

    private void whenRunGraphqlRepositoriesByExtensionsQuery(String... list) {
        String extensions = Arrays.asList(list).stream().map(item -> "\"" + item + "\"").collect(Collectors.joining(","));

        whenRunGraphqlQuery(getPayload("{\n" +
                "  repositoriesByExtensions (extensions: [" + extensions + "]) {\n" +
                "    id\n" +
                "  }\n" +
                "}"));

    }

    private void whenRunGraphqlRepositoryByUrlQuery() {
        whenRunGraphqlQuery(getPayload("{\n" +
                "  repositoryByUrl (repoUrl: \"" + REPO_URL + "\") {\n" +
                "    id\n" +
                "  }\n" +
                "}"));

    }

    private void whenRunGraphqlRepositoryByIdQuery() {
        whenRunGraphqlQuery(getPayload("{\n" +
                "  repositoryById (id: " + entity.id + ") {\n" +
                "    id\n" +
                "  }\n" +
                "}"));

    }

    private void whenRunGraphqlRepositoriesQuery() {
        whenRunGraphqlQuery(getPayload("{\n" +
                "  repositories {\n" +
                "    id\n" +
                "  }\n" +
                "}"));

    }

    private void whenRunGraphqlQuery(String request) {
        response = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request).post(PATH)
                .then().statusCode(HttpStatus.SC_OK)
                .and();
    }

    private void thenRepositoryIsFound() {
        response.body(containsString("\"id\":" + entity.id));
    }

    private void thenRepositoryIsNotFound() {
        response.body(not(containsString("\"id\":" + entity.id)));
    }

    private static String getPayload(String query) {
        return Json.createObjectBuilder().add("query", query).build().toString();
    }
}
