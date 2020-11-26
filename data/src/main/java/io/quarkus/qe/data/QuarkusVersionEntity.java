package io.quarkus.qe.data;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity(name = "quarkus_version")
public class QuarkusVersionEntity extends PanacheEntity {
    @OneToMany(mappedBy = "quarkusVersion")
    public Set<RepositoryEntity> repositories;
    @Column
    public String version;
}
