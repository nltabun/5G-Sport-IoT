package org.example.database.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "movesense")
public class Movesense {
    @Id
    private long id;

    public Movesense() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
