package org.example.database.repository;

import org.example.database.entity.Ecg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EcgRepository extends JpaRepository<Ecg, Long> {

    Ecg findById(long id);

    List<Ecg> findByTimestampUtcBetween(Long start, Long end);

}
