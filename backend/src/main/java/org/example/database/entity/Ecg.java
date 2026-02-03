package org.example.database.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import org.example.database.json.EcgDeserializer;
import org.example.database.json.EcgSerializer;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ecg")
@JsonSerialize(using = EcgSerializer.class)
@JsonDeserialize(using = EcgDeserializer.class)
public class Ecg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "timestamp_utc")
    private int timestampUtc;

    @Column(name = "timestamp_ms")
    private int timestampMs;

    @ManyToOne
    @JoinColumn(name = "pico_id")
    private Pico pico;

    @ManyToOne
    @JoinColumn(name = "movesense_id")
    private Movesense movesense;

    @Transient
    List<EcgSample> ecgSamples = new ArrayList<EcgSample>();

    public Ecg() {}

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public int getTimestampUtc() {
        return timestampUtc;
    }

    public void setTimestampUtc(int timestampUtc) {
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

    public List<EcgSample> getEcgSamples() {
        return ecgSamples;
    }

    public void setEcgSamples(List<EcgSample> samples) {
        ecgSamples = samples;
    }

    public void addEcgSample(EcgSample sample) {
        ecgSamples.add(sample);
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
