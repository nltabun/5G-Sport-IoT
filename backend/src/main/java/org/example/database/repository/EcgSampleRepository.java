// package org.example.database.repository;

// import org.example.database.entity.EcgSample;
// import org.springframework.data.repository.CrudRepository;

// import java.util.List;

// public interface EcgSampleRepository extends CrudRepository<EcgSample, Long> {
//     List<EcgSample> findByEcgId(long id);
// }
package org.example.database.repository;

import org.example.database.entity.EcgSample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EcgSampleRepository extends JpaRepository<EcgSample, Long> {

    List<EcgSample> findByEcgId(long id);

}