package com.serezka.database.repository.university;

import com.serezka.database.model.university.Flow;
import com.serezka.database.model.university.Person;
import com.serezka.database.model.university.Practice;
import com.serezka.database.model.university.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface PracticeRepository extends JpaRepository<Practice, Long> {
    @Query("SELECT p FROM Practice p WHERE p.begin BETWEEN :startTime AND :endTime")
    List<Practice> findPracticesByTimeRange(@Param("startTime") ZonedDateTime startTime,
                                            @Param("endTime") ZonedDateTime endTime);

    @Query("SELECT p FROM Practice p WHERE p.begin <= :startTime")
    List<Practice> findPracticesByTimeFrom(@Param("startTime") ZonedDateTime startTime);

    @Query("SELECT p FROM Practice p WHERE p.begin <= :startTime AND p.queue.state = :state")
    List<Practice> findPracticesByTimeFromAndQueueState(@Param("startTime") ZonedDateTime startTime,
                                                        @Param("state") Queue.State state);

    List<Practice> findAllByTeachersContaining(Person teacher);

    List<Practice> findAllByFlow(Flow flow);
}
