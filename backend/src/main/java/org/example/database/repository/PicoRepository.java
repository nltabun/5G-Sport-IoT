package org.example.database.repository;

import org.example.database.entity.Pico;
import org.springframework.data.repository.CrudRepository;

public interface PicoRepository extends CrudRepository<Pico, String> {
}
