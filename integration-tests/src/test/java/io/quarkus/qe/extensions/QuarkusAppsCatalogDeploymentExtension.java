package io.quarkus.qe.extensions;

import java.util.stream.Stream;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.lifecycle.Startables;

import io.quarkus.qe.containers.EnricherServiceContainer;
import io.quarkus.qe.containers.PostgreSqlContainer;
import io.quarkus.qe.containers.RestApiServiceContainer;
import io.quarkus.qe.containers.StorageServiceContainer;

public class QuarkusAppsCatalogDeploymentExtension implements BeforeAllCallback, AfterAllCallback {

    public static final String DATABASE_ALIAS = "database";
    public static final String KAFKA_ALIAS = "kafka";

    private Network network;
    private PostgreSqlContainer database;
    private KafkaContainer kafka;
    private StorageServiceContainer storageService;
    private RestApiServiceContainer restApiService;
    private EnricherServiceContainer enricherService;

    @SuppressWarnings({ "resource", "deprecation" })
    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        network = Network.newNetwork();
        database = new PostgreSqlContainer().withNetwork(network).withNetworkAliases(DATABASE_ALIAS);

        kafka = new KafkaContainer().withNetwork(network).withNetworkAliases(KAFKA_ALIAS);

        storageService = new StorageServiceContainer();
        storageService.withNetwork(network);
        storageService.dependsOn(database, kafka);

        enricherService = new EnricherServiceContainer();
        enricherService.withNetwork(network);
        enricherService.dependsOn(database, kafka);

        restApiService = new RestApiServiceContainer();
        restApiService.withNetwork(network);
        restApiService.dependsOn(database, kafka);

        Startables.deepStart(Stream.of(restApiService, enricherService, storageService, database, kafka)).join();
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        closeSilently(storageService);
        closeSilently(enricherService);
        closeSilently(restApiService);
        closeSilently(database);
        closeSilently(kafka);
        closeSilently(network);
    }

    public int getStorageServicePort() {
        return storageService.getMappedPort();
    }

    public int getRestApiPort() {
        return restApiService.getMappedPort();
    }

    public int getEnricherServicePort() {
        return enricherService.getMappedPort();
    }

    private static final void closeSilently(AutoCloseable object) {
        if (object != null) {
            try {
                object.close();
            } catch (Exception ignored) {
            }
        }
    }

}
