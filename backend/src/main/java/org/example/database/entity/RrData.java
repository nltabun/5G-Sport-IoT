package org.example.database.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rr_data")
public class RrData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int value;

    @ManyToOne
    @JoinColumn(name = "heart_rate_id")
    private HeartRate heartRate;

    public RrData() {}

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public HeartRate getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(HeartRate heartRate) {
        this.heartRate = heartRate;
    }

    @Override
    public String toString() {
        return "{ id: " + id
                + ", value: " + value
                + ", heart_rate_id: " + heartRate.getId()
                + " }";
    }
}
