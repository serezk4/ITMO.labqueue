package com.serezka.database.repository.university;

import com.serezka.database.model.university.Practice;
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

//    List<Practice> findAllByTeacher(Teacher teacher); todo
}
