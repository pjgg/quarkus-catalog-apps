package io.quarkus.qe.data;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "quarkus_extension")
public class QuarkusExtensionEntity extends PanacheEntity {
    @ManyToOne
    @JoinColumn(name = "repository_id", nullable = false)
    public RepositoryEntity repository;
    @Column(nullable = false)
    public String name;
    @ManyToOne
    @JoinColumn(name = "quarkus_version_id")
    public QuarkusVersionEntity version;

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof QuarkusExtensionEntity))
            return false;
        QuarkusExtensionEntity other = (QuarkusExtensionEntity) obj;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
