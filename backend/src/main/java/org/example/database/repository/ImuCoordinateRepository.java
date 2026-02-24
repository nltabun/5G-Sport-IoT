package org.example.database.repository;

import org.example.database.entity.ImuCoordinate;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ImuCoordinateRepository extends CrudRepository<ImuCoordinate, Long> {
    List<ImuCoordinate> findByImuId(long id);
}
