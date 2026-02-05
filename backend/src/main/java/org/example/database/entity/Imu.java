package org.example.database.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import org.example.database.json.ImuDeserializer;
import org.example.database.json.ImuSerializer;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "imu")
@JsonSerialize(using = ImuSerializer.class)
@JsonDeserialize(using = ImuDeserializer.class)
public class Imu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "timestamp_utc")
    private Long timestampUtc;

    @Column(name = "timestamp_ms")
    private int timestampMs;

    @ManyToOne
    @JoinColumn(name = "pico_id")
    private Pico pico;

    @ManyToOne
    @JoinColumn(name = "movesense_id")
    private Movesense movesense;

    @Transient
    List<ImuCoordinate> imuCoordinates = new ArrayList<ImuCoordinate>();

    public Imu() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimestampUtc() {
        return timestampUtc;
    }

    public void setTimestampUtc(Long timestampUtc) {
        this.timestampUtc = timestampUtc;
    }

    public int getTimestampMs() {
        return timestampMs;
    }

    public void setTimestampMs(int timestampMs) {
        this.timestampMs = timestampMs;
    }

    public Pico getPico() {
        return pico;
    }

    public void setPico(Pico pico) {
        this.pico = pico;
    }

    public Movesense getMovesense() {
        return movesense;
    }

    public void setMovesense(Movesense movesense) {
        this.movesense = movesense;
    }

    public List<ImuCoordinate> getImuCoordinates() {
        return imuCoordinates;
    }

    public void setImuCoordinates(List<ImuCoordinate> imuCoordinates) {
        this.imuCoordinates = imuCoordinates;
    }

    public void addImuCoordinate(ImuCoordinate coordinate) {
        imuCoordinates.add(coordinate);
    }

    @Override
    public String toString() {
        return "{ id: " + id
                + ", timestamp_utc: " + timestampUtc
                + ", timestamp_ms: " + timestampMs
                + ", pico_id: " + pico.getId()
                + ", movesense_id: " + movesense.getId()
                + " }";
    }
}
