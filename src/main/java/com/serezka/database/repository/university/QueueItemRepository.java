package com.serezka.database.repository.university;

import com.serezka.database.model.university.QueueItem;
import com.serezka.database.model.university.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueueItemRepository extends JpaRepository<QueueItem, Long> {
    List<QueueItem> findAllByStudent(Student student);
}
