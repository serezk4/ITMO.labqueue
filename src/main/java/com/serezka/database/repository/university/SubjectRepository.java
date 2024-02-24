package com.serezka.database.repository.university;

import com.serezka.database.model.university.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    boolean existsByName(String name);

    Subject findByName(String name);
}
