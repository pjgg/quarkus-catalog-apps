package io.quarkus.qe.data.marshallers;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.qe.data.QuarkusExtensionEntity;
import io.quarkus.qe.model.QuarkusExtension;

@ApplicationScoped
public class QuarkusExtensionMarshaller {

    public QuarkusExtension fromEntity(QuarkusExtensionEntity entity) {
        QuarkusExtension model = new QuarkusExtension();
        model.setName(entity.name);
        model.setVersion(entity.version);
        return model;
    }
}
