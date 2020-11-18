package io.quarkus.qe;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import io.quarkus.qe.model.Repository;

public class RepositoryWorkflowIT extends BaseIT {

    private static final String PATH = "/repository";
    private static final String REPO_URL = "https://github.com/quarkus-qe/quarkus-catalog-apps";

    @Test
    public void shouldCreateRepositoryAndPopulateRepository() {
        whenCreateNewRepository(REPO_URL);
        thenRepositoryShouldBeCreatedInDatabase(REPO_URL);

        thenRepositoryShouldBeUpdatedWithSomeComment(REPO_URL, "quarkus-catalog-apps");
    }

    private void whenCreateNewRepository(String repoUrl) {
        Repository repository = new Repository();
        repository.setRepoUrl(repoUrl);
        givenRestApiService().body(repository).post(PATH).then().statusCode(HttpStatus.SC_ACCEPTED);
    }

    private void thenRepositoryShouldBeCreatedInDatabase(String expectedRepoUrl) {
        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> assertTrue(getRepositoryByRepoUrl(expectedRepoUrl).isPresent()));
    }

    private void thenRepositoryShouldBeUpdatedWithSomeComment(String expectedRepoUrl, String expectedName) {
        await().atMost(30, TimeUnit.SECONDS).untilAsserted(
                () -> assertEquals(expectedName, getRepositoryByRepoUrl(expectedRepoUrl).get().getName()));
    }

    private Optional<Repository> getRepositoryByRepoUrl(String expectedRepoUrl) {
        Repository[] list = givenRestApiService().get(PATH).then().statusCode(HttpStatus.SC_OK).and().extract()
                .as(Repository[].class);

        return Stream.of(list).filter(repo -> expectedRepoUrl.equals(repo.getRepoUrl())).findFirst();
    }
}
