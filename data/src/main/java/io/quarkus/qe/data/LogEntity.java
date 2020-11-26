package io.quarkus.qe.data;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "log")
public class LogEntity extends PanacheEntity {
    @ManyToOne
    @JoinColumn(name = "repository_id", nullable = false)
    public RepositoryEntity repository;

    @Column(columnDefinition = "TIMESTAMP")
    public LocalDateTime timestamp;

    @Column(nullable = false)
    public String level;
    @Column(nullable = false)
    public String message;
}
