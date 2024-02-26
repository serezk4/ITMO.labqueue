package com.serezka.database.repository.university;

import com.serezka.database.model.university.Practice;
import com.serezka.database.model.university.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.List;

@Repository
public interface PracticeRepository extends JpaRepository<Practice, Long> {
    List<Practice> findAllByTeacher(Teacher teacher);
//    List<Practice> findAllByDateAndBegin_Hour(ZonedDateTime calendar); todo
}
