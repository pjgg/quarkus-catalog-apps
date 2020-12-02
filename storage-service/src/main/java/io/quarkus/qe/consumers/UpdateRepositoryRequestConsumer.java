package io.quarkus.qe.consumers;

import java.time.LocalDateTime;

import java.util.Objects;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import io.quarkus.qe.data.QuarkusVersionEntity;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.qe.configuration.Channels;
import io.quarkus.qe.data.LogEntity;
import io.quarkus.qe.data.QuarkusExtensionEntity;
import io.quarkus.qe.data.RepositoryEntity;
import io.quarkus.qe.data.RepositoryStatus;
import io.quarkus.qe.data.marshallers.LogMarshaller;
import io.quarkus.qe.model.Log;
import io.quarkus.qe.model.QuarkusExtension;
import io.quarkus.qe.model.Repository;
import io.smallrye.reactive.messaging.annotations.Blocking;

import java.util.Optional;

@ApplicationScoped
public class UpdateRepositoryRequestConsumer {

    private static final Logger LOG = Logger.getLogger(UpdateRepositoryRequestConsumer.class);

    @Inject
    LogMarshaller logMarshaller;

    @Incoming(Channels.UPDATE_REPOSITORY)
    @Blocking
    @Transactional
    public void addRepository(Repository repository) {
        LOG.info("Update repository " + repository.getRepoUrl());

        RepositoryEntity entity = RepositoryEntity.findById(repository.getId());
        entity.name = repository.getName();
        entity.updatedAt = LocalDateTime.now();
        entity.status = RepositoryStatus.COMPLETED;
        updateQuarkusExtensions(repository, entity);
        updateLogs(repository, entity);
        updateRepositoryVersion(repository, entity);
        entity.persist();
    }

    private void updateQuarkusExtensions(Repository repository, RepositoryEntity entity) {
        entity.extensions.clear();
        if (repository.getExtensions() != null) {
            for (QuarkusExtension extension : repository.getExtensions()) {
                QuarkusExtensionEntity extensionEntity = new QuarkusExtensionEntity();
                extensionEntity.repository = entity;
                extensionEntity.name = extension.getName();
                extensionEntity.version = getVersionEntity(extension.getVersion());
                if (Objects.isNull(extensionEntity.version)) {
                    QuarkusVersionEntity versionEntity = new QuarkusVersionEntity();
                    versionEntity.id = extension.getVersion();
                    versionEntity.persist();
                    extensionEntity.version = versionEntity;
                }
                entity.extensions.add(extensionEntity);
            }
        }
    }

    private void updateLogs(Repository repository, RepositoryEntity entity) {
        entity.logs.clear();
        entity.logs.forEach(PanacheEntityBase::delete);
        if (repository.getLogs() != null) {
            for (Log log : repository.getLogs()) {
                LogEntity logEntity = logMarshaller.fromModel(log);
                logEntity.repository = entity;
                entity.logs.add(logEntity);
            }
        }
    }

    private void updateRepositoryVersion(Repository repository, RepositoryEntity entity) {
        Optional.ofNullable(repository.getQuarkusVersion()).ifPresent(version -> {
            entity.quarkusVersion = getVersionEntity(version.getVersion());
            if (Objects.isNull(entity.quarkusVersion)) {
                QuarkusVersionEntity versionEntity = new QuarkusVersionEntity();
                versionEntity.id = version.getVersion();
                versionEntity.persist();
                entity.quarkusVersion = versionEntity;
            }
        });
    }

    private QuarkusVersionEntity getVersionEntity(String version) {
        return QuarkusVersionEntity.findById(version);
    }
}
