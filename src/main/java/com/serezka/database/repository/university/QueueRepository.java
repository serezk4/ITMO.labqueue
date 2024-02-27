package com.serezka.database.repository.university;

import com.serezka.database.model.university.Practice;
import com.serezka.database.model.university.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QueueRepository extends JpaRepository<Queue, Long> {
    Optional<Queue> findByPractice(Practice practice);
}
