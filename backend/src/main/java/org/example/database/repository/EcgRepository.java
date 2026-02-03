package org.example.database.repository;

import org.example.database.entity.Ecg;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EcgRepository extends CrudRepository<Ecg, Long> {
    Ecg findById(long id);
    List<Ecg> findByTimestampUtcBetween(int start, int end);
}
