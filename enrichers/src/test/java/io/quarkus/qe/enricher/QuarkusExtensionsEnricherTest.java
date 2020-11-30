package io.quarkus.qe.enricher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import static java.util.Arrays.asList;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.inject.Instance;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.quarkus.qe.exceptions.EnrichmentException;
import io.quarkus.qe.model.QuarkusExtension;
import io.quarkus.qe.model.Repository;
import io.quarkus.qe.services.RepoUrlToRawService;

@ExtendWith(MockitoExtension.class)
public class QuarkusExtensionsEnricherTest {

    private static final String REPO_URL = "https://github.com/user/repo";
    private static final String RAW_URL = REPO_URL + "/raw";
    private static final String BRANCH = "main";

    @Mock
    private RepoUrlToRawService rawUrlService;

    private Repository repository;
    private QuarkusExtensionsEnricher enricher;

    @SuppressWarnings("unchecked")
    @BeforeEach
    public void setup() {
        enricher = spy(new QuarkusExtensionsEnricher());
        enricher.repoUrlToRawServices = mock(Instance.class);
        enricher.quarkusVersionResolver = mock(QuarkusVersionResolver.class);
        when(enricher.repoUrlToRawServices.stream()).thenReturn(asList(rawUrlService).stream());

        repository = new Repository();
        repository.setRepoUrl(REPO_URL);
        repository.setBranch(BRANCH);
    }

    @Test
    public void shouldRaiseEnrichExceptionIfNotSupported() {
        givenRepositoryIsNotSupported();
        Assertions.assertThrows(EnrichmentException.class, this::whenEnrichRepository);
    }

    @Test
    public void shouldReturnExpectedExtensions() throws EnrichmentException {
        givenRepositoryIsSupported();
        givenModelFor(RAW_URL, asList("quarkus-extension1", "quarkus-extension2"), asList("module1"));
        givenModelFor(RAW_URL + "/module1", asList("quarkus-extension2", "quarkus-extension3"), emptyList());
        whenEnrichRepository();
        thenExpectedExtensionsAre("quarkus-extension1", "quarkus-extension2", "quarkus-extension3");
    }

    private void givenModelFor(String baseUrl, List<String> extensions, List<String> modules) throws EnrichmentException {
        Model model = new Model();

        extensions.forEach(extension -> {
            Dependency dependency = new Dependency();
            dependency.setArtifactId(extension);
            model.addDependency(dependency);
        });

        modules.forEach(model::addModule);

        doReturn(model).when(enricher).parseMavenModel(baseUrl);
    }

    private void givenRepositoryIsNotSupported() {
        when(rawUrlService.isFor(repository)).thenReturn(false);
    }

    private void givenRepositoryIsSupported() {
        when(rawUrlService.isFor(repository)).thenReturn(true);
        when(rawUrlService.getRawUrl(repository)).thenReturn(RAW_URL);
    }

    private void whenEnrichRepository() throws EnrichmentException {
        enricher.enrichRepository(repository);
    }

    private void thenExpectedExtensionsAre(String... expectedExtensions) {
        List<String> actualExtensions = repository.getExtensions().stream().map(QuarkusExtension::getName)
                .collect(Collectors.toList());

        assertEquals(expectedExtensions.length, repository.getExtensions().size());
        assertTrue(Stream.of(expectedExtensions).anyMatch(actualExtensions::contains));
    }

}
