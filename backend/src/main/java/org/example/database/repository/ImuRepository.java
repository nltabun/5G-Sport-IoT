package org.example.database.repository;

import org.example.database.entity.Imu;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ImuRepository extends CrudRepository<Imu, Long> {
    Imu findById(long id);
    List<Imu> findByTimestampUtcBetween(Long start, Long end);
}
