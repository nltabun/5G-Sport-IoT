package org.example.database.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "imu_coordinate")
public class ImuCoordinate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private double x;
    private double y;
    private double z;

    @ManyToOne
    @JoinColumn(name = "imu_id")
    private Imu imu;

    public ImuCoordinate() {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public Imu getImu() {
        return imu;
    }

    public void setImu(Imu imu) {
        this.imu = imu;
    }

    @Override
    public String toString() {
        return "{ id: " + id
                + ", type: " + type
                + ", x: " + x
                + ", y: " + y
                + ", z: " + z
                + ", imu_id: " + imu.getId()
                + " }";
    }
}
