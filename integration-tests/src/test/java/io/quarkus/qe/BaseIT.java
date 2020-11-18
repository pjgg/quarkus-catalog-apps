package io.quarkus.qe;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.quarkus.qe.extensions.QuarkusAppsCatalogDeploymentExtension;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.specification.RequestSpecification;

@Testcontainers
public class BaseIT {

    @RegisterExtension
    static final QuarkusAppsCatalogDeploymentExtension deployment = new QuarkusAppsCatalogDeploymentExtension();

    @BeforeAll
    public static void beforeAll() {
        RestAssured.defaultParser = Parser.JSON;
    }

    protected RequestSpecification givenStorageService() {
        return given().port(deployment.getStorageServicePort());
    }

    protected RequestSpecification givenRestApiService() {
        return given().contentType(ContentType.JSON).accept(ContentType.JSON).port(deployment.getRestApiPort());
    }

    protected RequestSpecification givenEnrichService() {
        return given().port(deployment.getEnricherServicePort());
    }
}
