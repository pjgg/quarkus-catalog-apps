package io.quarkus.qe.utils;

import io.quarkus.qe.data.QuarkusVersionEntity;
import java.util.Set;

import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import io.quarkus.qe.data.QuarkusExtensionEntity;
import io.quarkus.qe.data.RepositoryEntity;

@ApplicationScoped
public class RepositoryEntityUtils {

    @Transactional
    public void deleteAll() {
        QuarkusExtensionEntity.deleteAll();
        RepositoryEntity.deleteAll();
    }

    @Transactional
    public RepositoryEntity create(String repoUrl, String branch) {
        RepositoryEntity entity = new RepositoryEntity();
        entity.repoUrl = repoUrl;
        entity.branch = branch;
        entity.quarkusVersion = createVersionEntity(System.currentTimeMillis() + ".version");
        entity.persist();

        return entity;
    }

    @Transactional
    public QuarkusVersionEntity createVersionEntity(String version) {
        QuarkusVersionEntity versionEntity = new QuarkusVersionEntity();
        versionEntity.id = version;
        versionEntity.persist();

        return versionEntity;
    }

    @Transactional
    public RepositoryEntity updateExtensions(Long id, Set<String> extensions) {
        RepositoryEntity entity = RepositoryEntity.findById(id);
        entity.extensions.clear();

        for (String extensionName : extensions) {
            QuarkusExtensionEntity extensionEntity = new QuarkusExtensionEntity();
            extensionEntity.repository = entity;
            extensionEntity.name = extensionName;
            extensionEntity.version = entity.quarkusVersion;

            entity.extensions.add(extensionEntity);
        }

        entity.persist();

        return entity;
    }

    @Transactional
    public RepositoryEntity findById(Long id) {
        return RepositoryEntity.<RepositoryEntity> findById(id);
    }
}
