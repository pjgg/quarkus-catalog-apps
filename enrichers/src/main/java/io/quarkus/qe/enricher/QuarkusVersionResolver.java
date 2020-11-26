package io.quarkus.qe.enricher;

import io.quarkus.qe.model.Repository;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import org.apache.maven.model.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class QuarkusVersionResolver {

    private static final String QUARKUS_LABEL = "quarkus";
    private static final String POM_TYPE = "pom";
    private static final String MAVEN_VARIABLE = "(\\$\\{)(.+?)(\\})";
    private static final int MAVEN_VARIABLE_NAME = 2;
    private static final Pattern REGEXP_MAVEN_VARIABLE = Pattern.compile(MAVEN_VARIABLE);
    private static final String QUARKUS_UNIVERSE_ARTIFACT = "quarkus-universe-bom";
    private static final String QUARKUS_MAVEN_PLUGIN_ARTIFACT = "quarkus-maven-plugin";
    private static final Pattern REGEXP_QUARKUS_UNIVERSE_ARTIFACT = Pattern.compile(QUARKUS_UNIVERSE_ARTIFACT + "|quarkus-bom");
    private static final String QUARKUS_INTERNAL_MAVEN_PLUGIN_ARTIFACT = "__" + QUARKUS_MAVEN_PLUGIN_ARTIFACT;
    private static final String QUARKUS_INTERNAL_UNIVERSE_ARTIFACT = "__" + QUARKUS_UNIVERSE_ARTIFACT;
    private static final String EMPTY = "";

    private Map<String, String> lookUpTable = new HashMap<>();

    public void addAll(Properties prop) {
        prop.forEach((name, value) -> lookUpTable.put((String) name, (String) value));
    }

    public void addAll(DependencyManagement dependencyManagement) {
        Optional.ofNullable(dependencyManagement).ifPresent(dm -> addAll(dm.getDependencies()));
    }

    public void addAllQuarkusPlugins(Build buildBlock) {
        Optional.ofNullable(buildBlock).ifPresent(build -> {
            addQuarkusMavenPlugin(build.getPluginManagement());
            addQuarkusMavenPlugin(build.getPlugins());
        });
    }

    private void addAll(List<Dependency> dependencies) {
        dependencies.stream().forEach(dependency -> {
            String artifactId = extractMavenValue(dependency.getArtifactId());
            String version = extractMavenValue(dependency.getVersion());
            lookUpTable.put(artifactId, version);

            // looking for quarkus bom
            if (isQuarkusPom(dependency)) {
                Matcher matcher = REGEXP_QUARKUS_UNIVERSE_ARTIFACT.matcher(artifactId);
                if (matcher.find()) {
                    lookUpTable.put(QUARKUS_INTERNAL_UNIVERSE_ARTIFACT, version);
                }
            }
        });
    }

    public String extractMavenValue(String value) {
        Matcher matcher = REGEXP_MAVEN_VARIABLE.matcher(Optional.ofNullable(value).orElse(EMPTY));
        if (matcher.find()) {
            String propertyName = matcher.group(MAVEN_VARIABLE_NAME);
            value = lookUpTable.get(propertyName);
        }

        return value;
    }

    public boolean containsQuarkusMavenPlugin() {
        return lookUpTable.containsKey(QUARKUS_INTERNAL_MAVEN_PLUGIN_ARTIFACT);
    }

    public String getQuarkusPluginMavenVersion() {
        return lookUpTable.get(QUARKUS_INTERNAL_MAVEN_PLUGIN_ARTIFACT);
    }

    public String getQuarkusBomVersion() {
        return lookUpTable.get(QUARKUS_INTERNAL_UNIVERSE_ARTIFACT);
    }

    public String getOverAllQuarkusVersion(Repository repository) {
        String version = Optional.ofNullable(getQuarkusBomVersion()).orElse(getQuarkusPluginMavenVersion());
        if (Objects.isNull(version))
            version = mostPopularQuarkusExtVersion(repository);
        return version;
    }

    public boolean containsQuarkusBom() {
        return lookUpTable.containsKey(QUARKUS_INTERNAL_UNIVERSE_ARTIFACT);
    }

    public String getVersionFromArtifactId(String artifactId) {
        return lookUpTable.get(artifactId);
    }

    private String mostPopularQuarkusExtVersion(Repository repository) {
        Map<String, Long> occurrences = repository.getExtensions().stream()
                .collect(Collectors.groupingBy(dependency -> dependency.getVersion(), Collectors.counting()));

        return Collections.max(occurrences.entrySet(), Comparator.comparingLong(item -> item.getValue())).getKey();
    }

    private boolean isQuarkusPom(Dependency dependency) {
        return dependency.getArtifactId().contains(QUARKUS_LABEL) && dependency.getType().equalsIgnoreCase(POM_TYPE);
    }

    private void addQuarkusMavenPlugin(PluginManagement pluginManagement) {
        Optional.ofNullable(pluginManagement).ifPresent(pm -> addQuarkusMavenPlugin(pm.getPlugins()));
    }

    private void addQuarkusMavenPlugin(List<Plugin> plugins) {
        if (!lookUpTable.containsKey(QUARKUS_INTERNAL_MAVEN_PLUGIN_ARTIFACT)) {
            for (var plugin : plugins) {
                String artifactId = extractMavenValue(plugin.getArtifactId());
                String version = extractMavenValue(plugin.getVersion());

                // looking for quarkus Maven plugin
                if (QUARKUS_MAVEN_PLUGIN_ARTIFACT.equalsIgnoreCase(artifactId)) {
                    lookUpTable.put(QUARKUS_INTERNAL_MAVEN_PLUGIN_ARTIFACT, version);
                    break;
                }
            }
        }
    }
}
