package io.quarkus.qe.data;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "quarkus_version")
public class QuarkusVersionEntity extends PanacheEntityBase {
    @Id
    public String id;

    public QuarkusVersionEntity() {
    }

    public QuarkusVersionEntity(String id) {
        this.id = id;
    }
}
