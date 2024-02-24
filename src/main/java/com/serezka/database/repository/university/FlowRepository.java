package com.serezka.database.repository.university;

import com.serezka.database.model.university.Flow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowRepository extends JpaRepository<Flow, Long> {
    boolean existsByName(String name);
    Flow findByName(String name);
}
