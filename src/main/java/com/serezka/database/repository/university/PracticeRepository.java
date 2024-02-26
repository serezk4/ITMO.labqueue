package com.serezka.database.repository.university;

import com.serezka.database.model.university.Practice;
import com.serezka.database.model.university.Teacher;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.List;

@Repository
public interface PracticeRepository extends JpaRepository<Practice, Long> {
    @Query("SELECT p FROM Practice p WHERE p.date = :dateParam AND p.begin BETWEEN :startTime AND :endTime")
    List<Practice> findPracticesByDateAndTimeRange(@Param("dateParam") LocalDate date,
                                                   @Param("startTime") ZonedDateTime startTime,
                                                   @Param("endTime") ZonedDateTime endTime);

    List<Practice> findAllByTeacher(Teacher teacher);
}
