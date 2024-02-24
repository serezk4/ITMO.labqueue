package com.serezka.database.repository.university;

import com.serezka.database.model.university.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    boolean existsByName(String name);
    Group findByName(String name);
}
