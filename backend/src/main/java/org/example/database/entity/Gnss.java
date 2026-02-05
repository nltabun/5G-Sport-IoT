package org.example.database.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import org.example.database.json.GnssDeserializer;
import org.example.database.json.GnssSerializer;

@Entity
@Table(name = "gnss")
@JsonSerialize(using = GnssSerializer.class)
@JsonDeserialize(using = GnssDeserializer.class)
public class Gnss {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double latitude;

    private double longitude;

    @Column(name = "fix_q")
    private int fixQ;

    @Column(name = "timestamp_utc")
    private Long timestampUtc;

    @Column(name = "timestamp_ms")
    private int timestampMs;

    @ManyToOne
    @JoinColumn(name = "pico_id")
    private Pico pico;

    public Gnss() { }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getFixQ() {
        return fixQ;
    }

    public void setFixQ(int fixQ) {
        this.fixQ = fixQ;
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

    @Override
    public String toString() {
        return "{ id: " + id
                + ", latitude: " + latitude
                + ", longitude: " + longitude
                + ", fixQ: " + fixQ
                + ", timestamp_utc: " + timestampUtc
                + ", timestamp_ms: " + timestampMs
                + ", pico_id: " + pico.getId()
                + " }";
    }
}
