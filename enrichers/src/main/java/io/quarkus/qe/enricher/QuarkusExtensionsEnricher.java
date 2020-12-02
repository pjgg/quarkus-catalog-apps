package io.quarkus.qe.enricher;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import io.quarkus.qe.model.QuarkusVersion;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import io.quarkus.qe.exceptions.EnrichmentException;
import io.quarkus.qe.model.QuarkusExtension;
import io.quarkus.qe.model.Repository;
import io.quarkus.qe.services.RepoUrlToRawService;

@ApplicationScoped
public class QuarkusExtensionsEnricher implements Enricher {

    private static final String QUARKUS_TAG = "quarkus-";
    private static final String POM_XML = "/pom.xml";

    @Inject
    Instance<RepoUrlToRawService> repoUrlToRawServices;

    @Override
    public String type() {
        return "Quarkus Extensions";
    }

    @Override
    public void enrichRepository(Repository repository) throws EnrichmentException {
        QuarkusVersionResolver quarkusVersionResolver = new QuarkusVersionResolver();
        String rawUrl = getRawUrlFromRepository(repository);
        repository.setExtensions(findAllDependencies(rawUrl, quarkusVersionResolver));
        repository.setQuarkusVersion(new QuarkusVersion(quarkusVersionResolver.getOverAllQuarkusVersion(repository)));
    }

    protected Model parseMavenModel(String baseUrl) throws EnrichmentException {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        try (InputStreamReader is = new InputStreamReader(new URL(baseUrl + POM_XML).openStream())) {
            return reader.read(is);
        } catch (IOException | XmlPullParserException e) {
            throw new EnrichmentException("Error reading the POM.xml at '%s': %s", baseUrl, e.getMessage());
        }
    }

    private String getRawUrlFromRepository(Repository repository) throws EnrichmentException {
        return repoUrlToRawServices.stream().filter(service -> service.isFor(repository))
                .map(service -> service.getRawUrl(repository)).findFirst()
                .orElseThrow(() -> new EnrichmentException("The %s repository is not supported. Can't get the RAW format. ",
                        repository.getRepoUrl()));
    }

    protected Set<QuarkusExtension> findAllDependencies(String baseUrl, QuarkusVersionResolver quarkusVersionResolver)
            throws EnrichmentException {
        Set<QuarkusExtension> extensions = new HashSet<>();

        Model model = parseMavenModel(baseUrl);
        quarkusVersionResolver.addAll(model.getProperties());
        quarkusVersionResolver.addAll(model.getDependencyManagement());
        quarkusVersionResolver.addAllQuarkusPlugins(model.getBuild());

        extensions.addAll(model.getDependencies().stream()
                .filter(onlyQuarkusDependencies())
                .map(e -> toQuarkusExtensionModel(e, quarkusVersionResolver))
                .collect(Collectors.toList()));

        for (String module : model.getModules()) {
            extensions.addAll(findAllDependencies(baseUrl + "/" + module, quarkusVersionResolver));
        }

        return extensions;
    }

    private Predicate<Dependency> onlyQuarkusDependencies() {
        return dependency -> dependency.getArtifactId().startsWith(QUARKUS_TAG);
    }

    private QuarkusExtension toQuarkusExtensionModel(Dependency extension, QuarkusVersionResolver quarkusVersionResolver) {
        String version = getDependencyVersion(extension, quarkusVersionResolver);
        QuarkusExtension model = new QuarkusExtension();
        model.setName(extension.getArtifactId());
        model.setVersion(version);
        return model;
    }

    private String getDependencyVersion(Dependency extension, QuarkusVersionResolver quarkusVersionResolver) {
        String version = quarkusVersionResolver.extractMavenValue(Optional.ofNullable(extension.getVersion())
                .orElse(quarkusVersionResolver.getVersionFromArtifactId(extension.getArtifactId())));

        if (version == null && quarkusVersionResolver.containsQuarkusBom()) {
            version = quarkusVersionResolver.getQuarkusBomVersion();
        }

        if (version == null && quarkusVersionResolver.containsQuarkusMavenPlugin()) {
            version = quarkusVersionResolver.getQuarkusPluginMavenVersion();
        }

        return version;
    }
}
