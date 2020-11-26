package io.quarkus.qe.data.marshallers;

import io.quarkus.qe.data.QuarkusVersionEntity;
import io.quarkus.qe.model.QuarkusVersion;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class QuarkusVersionMarshaller {
    public QuarkusVersion fromEntity(QuarkusVersionEntity entity) {
        QuarkusVersion model = new QuarkusVersion();
        model.setVersion(entity.version);
        return model;
    }
}
