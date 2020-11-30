package io.quarkus.qe;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

public class SearchIT extends BaseIT {

    private static final String GRAPHQL_PATH = "/graphql-ui";

    @Test
    public void shouldGraphqlBeAvailable() {
        awaitFor(() -> givenRestApiService().get(GRAPHQL_PATH).then().statusCode(HttpStatus.SC_OK));
    }
}
