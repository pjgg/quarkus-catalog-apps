package io.quarkus.qe.containers;

import static io.quarkus.qe.extensions.QuarkusAppsCatalogDeploymentExtension.DATABASE_ALIAS;
import static io.quarkus.qe.extensions.QuarkusAppsCatalogDeploymentExtension.KAFKA_ALIAS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

public abstract class BaseServiceContainer extends GenericContainer<BaseServiceContainer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseServiceContainer.class);

    private static final String IMAGE = "container.image.";
    private static final String DATABASE_URL_TEMPLATE = "jdbc:postgresql://%s:%s/quarkusappcatalog";

    private final int port;

    public BaseServiceContainer(String name, int port) {
        super(System.getProperty(IMAGE + name, getDefaultImageValue(name)));
        this.port = port;

        withLogConsumer(new Slf4jLogConsumer(LOGGER));
        waitingFor(Wait.forLogMessage(".*Listening on:.*", 1));
        withEnv("QUARKUS_DATASOURCE_JDBC_URL",
                String.format(DATABASE_URL_TEMPLATE, DATABASE_ALIAS, PostgreSqlContainer.PORT));
        withEnv("KAFKA_BOOTSTRAP_SERVERS", KAFKA_ALIAS + ":9092");
        withExposedPorts(port);
    }

    public int getMappedPort() {
        return getMappedPort(port);
    }

    private static final String getDefaultImageValue(String name) {
        return String.format("quarkus-qe/quarkus-apps-catalog-%s:1.0.0-SNAPSHOT", name);
    }
}
