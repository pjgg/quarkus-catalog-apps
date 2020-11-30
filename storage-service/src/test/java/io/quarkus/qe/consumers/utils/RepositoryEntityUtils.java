package io.quarkus.qe.consumers.utils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import io.quarkus.qe.data.LabelEntity;
import io.quarkus.qe.data.LogEntity;
import io.quarkus.qe.data.QuarkusExtensionEntity;
import io.quarkus.qe.data.RepositoryEntity;

@ApplicationScoped
public class RepositoryEntityUtils {

    @Transactional
    public void deleteAll() {
        LabelEntity.deleteAll();
        QuarkusExtensionEntity.deleteAll();
        LogEntity.deleteAll();
        RepositoryEntity.deleteAll();
    }

    @Transactional
    public RepositoryEntity create(String repoUrl) {
        RepositoryEntity entity = new RepositoryEntity();
        entity.repoUrl = repoUrl;
        entity.persist();

        return entity;
    }

    @Transactional
    public RepositoryEntity findById(Long id) {
        return RepositoryEntity.<RepositoryEntity> findById(id);
    }

    @Transactional
    public RepositoryEntity updateExtensions(Long id, Set<String> extensions) {
        RepositoryEntity entity = RepositoryEntity.findById(id);
        entity.extensions.clear();

        for (String extensionName : extensions) {
            QuarkusExtensionEntity extensionEntity = new QuarkusExtensionEntity();
            extensionEntity.repository = entity;
            extensionEntity.name = extensionName;

            entity.extensions.add(extensionEntity);
        }

        entity.persist();

        return entity;
    }

    @Transactional
    public RepositoryEntity updateLogs(Long id, List<String> logs) {
        RepositoryEntity entity = RepositoryEntity.findById(id);
        entity.logs.clear();

        for (String logMessage : logs) {
            LogEntity logEntity = new LogEntity();
            logEntity.repository = entity;
            logEntity.level = "INFO";
            logEntity.message = logMessage;
            logEntity.timestamp = LocalDateTime.now();

            entity.logs.add(logEntity);
        }

        entity.persist();

        return entity;
    }

    @Transactional
    public List<String> getAllLogs(Long id) {
        RepositoryEntity entityInCurrentSession = RepositoryEntity.findById(id);
        return entityInCurrentSession.logs.stream().map(e -> e.message).collect(Collectors.toList());
    }
}
