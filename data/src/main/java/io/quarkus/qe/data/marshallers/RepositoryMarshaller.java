package io.quarkus.qe.data.marshallers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.quarkus.qe.data.RepositoryEntity;
import io.quarkus.qe.model.Repository;

import java.util.Optional;

@ApplicationScoped
public class RepositoryMarshaller {

    @Inject
    QuarkusExtensionMarshaller quarkusExtensionMarshaller;

    @Inject
    QuarkusVersionMarshaller quarkusVersionMarshaller;

    public Repository fromEntity(RepositoryEntity entity) {
        Repository model = new Repository();
        model.setId(entity.id);
        model.setRepoUrl(entity.repoUrl);
        model.setBranch(entity.branch);
        model.setName(entity.name);
        Optional.ofNullable(entity.quarkusVersion)
                .ifPresent(version -> model.setGlobalVersion(quarkusVersionMarshaller.fromEntity(version)));

        if (entity.extensions != null) {
            entity.extensions.stream().map(quarkusExtensionMarshaller::fromEntity).forEach(model.getExtensions()::add);
        }

        return model;
    }

}
