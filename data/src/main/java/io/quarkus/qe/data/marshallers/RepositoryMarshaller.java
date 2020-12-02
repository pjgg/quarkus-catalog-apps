package io.quarkus.qe.data.marshallers;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.quarkus.qe.data.RepositoryEntity;
import io.quarkus.qe.model.Repository;

@ApplicationScoped
public class RepositoryMarshaller {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    private static final String EMPTY = "";

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
        model.setCreatedAt(formatDate(entity.createdAt));
        model.setUpdatedAt(formatDate(entity.updatedAt));
        Optional.ofNullable(entity.quarkusVersion)
                .ifPresent(version -> model.setQuarkusVersion(quarkusVersionMarshaller.fromEntity(version)));

        if (entity.status != null) {
            model.setStatus(entity.status.name());
        }

        if (entity.extensions != null) {
            entity.extensions.stream().map(quarkusExtensionMarshaller::fromEntity).forEach(model.getExtensions()::add);
        }

        if (entity.labels != null) {
            entity.labels.stream().map(label -> label.name).forEach(model.getLabels()::add);
        }

        return model;
    }

    private String formatDate(TemporalAccessor date) {
        return Optional.ofNullable(date).map(DATE_FORMATTER::format).orElse(EMPTY);
    }

}
