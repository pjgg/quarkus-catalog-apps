package io.quarkus.qe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    public void shouldCreateRepositoryAndPopulateRepository() {
        whenCreateNewRepository(REPO_URL);
        thenRepositoryShouldBeCreatedInDatabase(REPO_URL);

        thenRepositoryShouldBeUpdated(REPO_URL, "quarkus-catalog-apps");
    }

    private void whenCreateNewRepository(String repoUrl) {
        NewRepositoryRequest repository = new NewRepositoryRequest();
        repository.setRepoUrl(repoUrl);
        repository.setBranch(BRANCH);
        givenRestApiService().body(repository).post(PATH).then().statusCode(HttpStatus.SC_ACCEPTED);
    }

    private void thenRepositoryShouldBeCreatedInDatabase(String expectedRepoUrl) {
        awaitFor(() -> assertTrue(getRepositoryByRepoUrl(expectedRepoUrl).isPresent()));
    }

    private void thenRepositoryShouldBeUpdated(String expectedRepoUrl, String expectedName) {
        awaitFor(() -> {
            Repository actual = getRepositoryByRepoUrl(expectedRepoUrl).get();
            assertEquals(expectedName, actual.getName());
            assertNotNull(actual.getExtensions());
            assertFalse(actual.getExtensions().isEmpty());
        });
    }

    private Optional<Repository> getRepositoryByRepoUrl(String expectedRepoUrl) {
        Repository[] list = givenRestApiService().get(PATH).then().statusCode(HttpStatus.SC_OK).and().extract()
                .as(Repository[].class);

        return Stream.of(list).filter(repo -> expectedRepoUrl.equals(repo.getRepoUrl())).findFirst();
    }
}
