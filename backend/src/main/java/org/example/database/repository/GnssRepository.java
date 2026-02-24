package org.example.database.repository;

import org.example.database.entity.Gnss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GnssRepository extends JpaRepository<Gnss, Long> {

    List<Gnss> findByTimestampUtcBetween(Long start, Long end);
    List<Gnss> findByTimestampUtcBetween(long start, long end);
    Gnss findById(long id);
}
