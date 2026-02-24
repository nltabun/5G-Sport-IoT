// package org.example.database.repository;

// import org.example.database.entity.HeartRate;
// import org.springframework.data.repository.CrudRepository;

// import java.util.List;

// public interface HeartRateRepository extends CrudRepository<HeartRate, Long> {
//     HeartRate findById(long id);
//     List<HeartRate> findByTimestampUtcBetween(Long start, Long end);
// }

package org.example.database.repository;

import org.example.database.entity.HeartRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeartRateRepository extends JpaRepository<HeartRate, Long> {

    HeartRate findById(long id);

    List<HeartRate> findByTimestampUtcBetween(Long start, Long end);
}
