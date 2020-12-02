package io.quarkus.qe.enricher;

import io.quarkus.qe.exceptions.EnrichmentException;
import io.quarkus.qe.model.QuarkusExtension;
import io.quarkus.test.junit.QuarkusTest;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class FindAllDependenciesTest {

    private static final String VERSION_FROM_BOM = "versionFromBom/";
    private static final String VERSION_FROM_DEPENDENCY = "versionFromDependency/";
    private static final String VERSION_FROM_QUARKUS_MAVEN_PLUGIN = "versionFromQuarkusMavenPlugin/";
    private static final String VERSION_FROM_REAL_POM = "realPomExample/";
    private static final String VERSION_FROM_MULTI_MODULE = "multiModuleExample/";

    @Inject
    private QuarkusExtensionsEnricher enricher;

    @Test
    public void shouldFindVersionFromBomVersion() throws EnrichmentException {

        Set<QuarkusExtension> extensions = getQuarkusExtensions(VERSION_FROM_BOM);

        assertEquals(extensions.size(), 1, "Expected one extension, found " + extensions.size());
        QuarkusExtension ext = extensions.stream().findFirst().get();

        assertEquals(ext.getName(), "quarkus-junit5", "Expected quarkus-junit5, found " + ext.getName());
        assertEquals(ext.getVersion(), "1.10.0.Final", "Expected 1.10.0.Final, found " + ext.getVersion());
    }

    @Test
    public void shouldFindVersionFromDepVersion() throws EnrichmentException {

        Set<QuarkusExtension> extensions = getQuarkusExtensions(VERSION_FROM_DEPENDENCY);

        assertEquals(extensions.size(), 1, "Expected one extension, found " + extensions.size());
        QuarkusExtension ext = extensions.stream().findFirst().get();

        assertEquals(ext.getName(), "quarkus-junit5", "Expected quarkus-junit5, found " + ext.getName());
        assertEquals(ext.getVersion(), "1.9.1.Final", "Expected 1.9.1.Final, found " + ext.getVersion());
    }

    @Test
    public void shouldFindVersionFromQuarkusMavenPluginVersion() throws EnrichmentException {

        Set<QuarkusExtension> extensions = getQuarkusExtensions(VERSION_FROM_QUARKUS_MAVEN_PLUGIN);

        assertEquals(extensions.size(), 1, "Expected one extension, found " + extensions.size());
        QuarkusExtension ext = extensions.stream().findFirst().get();

        assertEquals(ext.getName(), "quarkus-junit5", "Expected quarkus-junit5, found " + ext.getName());
        assertEquals(ext.getVersion(), "1.10.0.Final", "Expected 1.10.0.Final, found " + ext.getVersion());
    }

    @Test
    public void shouldFindQuarkusVersionDependenciesRandomExample() throws EnrichmentException {

        Set<QuarkusExtension> extensions = getQuarkusExtensions(VERSION_FROM_REAL_POM);
        Set<QuarkusExtension> expectedExtensions = new HashSet<>();
        expectedExtensions.add(new QuarkusExtension("quarkus-resteasy-jsonb", "1.9.1.Final"));
        expectedExtensions.add(new QuarkusExtension("quarkus-rest-client", "1.9.1.Final"));
        expectedExtensions.add(new QuarkusExtension("quarkus-openshift", "1.9.1.Final"));
        expectedExtensions.add(new QuarkusExtension("quarkus-smallrye-health", "1.9.1.Final"));
        expectedExtensions.add(new QuarkusExtension("quarkus-smallrye-context-propagation", "1.9.1.Final"));
        expectedExtensions.add(new QuarkusExtension("quarkus-resteasy", "1.9.1.Final"));
        expectedExtensions.add(new QuarkusExtension("quarkus-junit5", "1.9.0.Final"));

        assertThat(extensions).hasSameElementsAs(expectedExtensions);
    }

    @Test
    public void shouldFindQuarkusVersionDependenciesMultiModuleExample() throws EnrichmentException {

        Set<QuarkusExtension> extensions = getQuarkusExtensions(VERSION_FROM_MULTI_MODULE);
        Set<QuarkusExtension> expectedExtensions = new HashSet<>();
        expectedExtensions.add(new QuarkusExtension("quarkus-container-image-jib", "1.10.0.Final"));
        expectedExtensions.add(new QuarkusExtension("quarkus-test-h2", "1.10.0.Final"));
        expectedExtensions.add(new QuarkusExtension("quarkus-jdbc-postgresql", "1.10.0.Final"));
        expectedExtensions.add(new QuarkusExtension("quarkus-jsonb", "1.10.0.Final"));
        expectedExtensions.add(new QuarkusExtension("quarkus-smallrye-reactive-messaging-kafka", "1.10.0.Final"));
        expectedExtensions.add(new QuarkusExtension("quarkus-smallrye-openapi", "1.10.0.Final"));
        expectedExtensions.add(new QuarkusExtension("quarkus-junit5", "1.10.0.Final"));
        expectedExtensions.add(new QuarkusExtension("quarkus-resteasy-jsonb", "1.10.0.Final"));
        expectedExtensions.add(new QuarkusExtension("quarkus-flyway", "1.10.0.Final"));
        expectedExtensions.add(new QuarkusExtension("quarkus-rest-client", "1.10.0.Final"));
        expectedExtensions.add(new QuarkusExtension("quarkus-smallrye-health", "1.10.0.Final"));
        expectedExtensions.add(new QuarkusExtension("quarkus-hibernate-orm-panache", "1.10.0.Final"));
        expectedExtensions.add(new QuarkusExtension("quarkus-jdbc-h2", "1.10.0.Final"));
        expectedExtensions.add(new QuarkusExtension("quarkus-hibernate-validator", "1.10.0.Final"));

        assertThat(extensions).hasSameElementsAs(expectedExtensions);
    }

    private Set<QuarkusExtension> getQuarkusExtensions(String relativePath) throws EnrichmentException {
        URL pomFile = getClass().getClassLoader().getResource(relativePath);
        QuarkusVersionResolver quarkusVersionResolver = new QuarkusVersionResolver();
        return enricher.findAllDependencies("file:///" + pomFile.getFile(), quarkusVersionResolver);
    }
}
