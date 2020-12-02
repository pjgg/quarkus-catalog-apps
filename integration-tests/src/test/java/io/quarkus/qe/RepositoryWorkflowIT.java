package io.quarkus.qe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import io.quarkus.qe.model.Repository;
import io.quarkus.qe.model.requests.NewRepositoryRequest;

public class RepositoryWorkflowIT extends BaseIT {

    private static final String PATH = "/repository";
    private static final String REPO_URL = "https://github.com/quarkus-qe/quarkus-catalog-apps";
    private static final String BRANCH = "main";
    private static final String EXPECTED_QUARKUS_VERSION = "1.10.0.Final";
    private static final String LABEL = "my-label";
    private static final String COMPLETED_STATE = "COMPLETED";
    private static final String PENDING_STATE = "PENDING";

    @Test
    public void shouldCreateRepositoryAndPopulateRepository() {
        whenCreateNewRepository(REPO_URL);
        thenRepositoryShouldBeCreatedInDatabase(REPO_URL);

        thenRepositoryShouldBeUpdated(REPO_URL, "quarkus-catalog-apps", EXPECTED_QUARKUS_VERSION);
    }

    private void whenCreateNewRepository(String repoUrl) {
        NewRepositoryRequest repository = new NewRepositoryRequest();
        repository.setRepoUrl(repoUrl);
        repository.setBranch(BRANCH);
        repository.setLabels(Arrays.asList(LABEL));
        givenRestApiService().body(repository).post(PATH).then().statusCode(HttpStatus.SC_ACCEPTED);
    }

    private void thenRepositoryShouldBeCreatedInDatabase(String expectedRepoUrl) {
        awaitFor(() -> {
            Optional<Repository> actual = getRepositoryByRepoUrl(expectedRepoUrl);
            assertTrue(actual.isPresent());
            assertNotNull(actual.get().getCreatedAt());
            assertEquals(PENDING_STATE, actual.get().getStatus());
        });
    }

    private void thenRepositoryShouldBeUpdated(String expectedRepoUrl, String expectedName, String expectedVersion) {
        awaitFor(() -> {
            Repository actual = getRepositoryByRepoUrl(expectedRepoUrl).get();
            assertEquals(expectedName, actual.getName());
            assertEquals(expectedVersion, actual.getQuarkusVersion().getVersion());
            assertTrue(!actual.getExtensions().isEmpty());
            // https://github.com/quarkus-qe/quarkus-catalog-apps/issues/20
            // assertEquals(expectedName, actual.getName());
            assertNotNull(actual.getExtensions());
            assertFalse(actual.getExtensions().isEmpty());
            assertEquals(1, actual.getLabels().size());
            assertEquals(LABEL, actual.getLabels().iterator().next());
            assertNotNull(actual.getUpdatedAt());
            assertEquals(COMPLETED_STATE, actual.getStatus());
        });
    }

    private Optional<Repository> getRepositoryByRepoUrl(String expectedRepoUrl) {
        Repository[] list = givenRestApiService().get(PATH).then().statusCode(HttpStatus.SC_OK).and().extract()
                .as(Repository[].class);

        return Stream.of(list).filter(repo -> expectedRepoUrl.equals(repo.getRepoUrl())).findFirst();
    }
}
