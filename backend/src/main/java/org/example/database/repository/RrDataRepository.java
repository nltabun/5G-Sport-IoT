package org.example.database.repository;

import org.example.database.entity.RrData;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RrDataRepository extends CrudRepository<RrData, Long> {
    List<RrData> findByHeartRateId(long id);
}
