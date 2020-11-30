package io.quarkus.qe;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

public class HealthIT extends BaseIT {

    private static final String HEALTH_PATH = "/health";

    @Test
    public void storageServiceHealthEndpointShouldBeOk() {
        awaitFor(() -> givenStorageService().get(HEALTH_PATH).then().statusCode(HttpStatus.SC_OK));
    }

    @Test
    public void restApiHealthEndpointShouldBeOk() {
        awaitFor(() -> givenRestApiService().get(HEALTH_PATH).then().statusCode(HttpStatus.SC_OK));
    }

    @Test
    public void enricherServiceHealthEndpointShouldBeOk() {
        awaitFor(() -> givenEnrichService().get(HEALTH_PATH).then().statusCode(HttpStatus.SC_OK));
    }
}
