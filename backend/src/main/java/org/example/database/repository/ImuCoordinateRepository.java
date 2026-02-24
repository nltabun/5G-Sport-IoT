package org.example.database.repository;

import org.example.database.entity.ImuCoordinate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImuCoordinateRepository extends JpaRepository<ImuCoordinate, Long> {

    List<ImuCoordinate> findByImuId(long id);

}