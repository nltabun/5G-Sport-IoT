package org.example.database.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ecg_sample")
public class EcgSample {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int value;

    @ManyToOne
    @JoinColumn(name = "ecg_id")
    private Ecg ecg;

    public EcgSample() {}

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Ecg getEcg() {
        return ecg;
    }

    public void setEcg(Ecg ecg) {
        this.ecg = ecg;
    }

    @Override
    public String toString() {
        return "{ id: " + id
                + ", value: " + value
                + ", ecg_id: " + ecg.getId()
                + " }";
    }
}
