package io.quarkus.qe.containers;

import org.testcontainers.containers.GenericContainer;

public class PostgreSqlContainer extends GenericContainer<PostgreSqlContainer> {

    public static final int PORT = 5432;

    public PostgreSqlContainer() {
        super("postgres:10.5");

        withEnv("POSTGRES_USER", "sarah");
        withEnv("POSTGRES_PASSWORD", "connor");
        withEnv("POSTGRES_DB", "quarkusappcatalog");
    }
}
