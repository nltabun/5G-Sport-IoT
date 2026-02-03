package org.example.database.repository;

import org.example.database.entity.Gnss;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GnssRepository extends CrudRepository<Gnss, Long> {
    Gnss findById(long id);
    List<Gnss> findByTimestampUtcBetween(int start, int end);
}
