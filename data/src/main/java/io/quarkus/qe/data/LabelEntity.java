package io.quarkus.qe.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name = "label")
public class LabelEntity extends PanacheEntity {
    @ManyToOne
    @JoinColumn(name = "repository_id", nullable = false)
    public RepositoryEntity repository;

    @Column(nullable = false)
    public String name;
}
