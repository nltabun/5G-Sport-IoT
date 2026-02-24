// package org.example.database.repository;

// import org.example.database.entity.RrData;
// import org.springframework.data.repository.CrudRepository;

// import java.util.List;

// public interface RrDataRepository extends CrudRepository<RrData, Long> {
//     List<RrData> findByHeartRateId(long id);
// }

package org.example.database.repository;

import org.example.database.entity.RrData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RrDataRepository extends JpaRepository<RrData, Long> {

    List<RrData> findByHeartRateId(long id);

}
