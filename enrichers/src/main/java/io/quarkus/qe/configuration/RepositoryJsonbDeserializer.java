package io.quarkus.qe.configuration;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;
import io.quarkus.qe.model.Repository;

public class RepositoryJsonbDeserializer extends JsonbDeserializer<Repository> {
    public RepositoryJsonbDeserializer() {
        super(Repository.class);
    }
}
