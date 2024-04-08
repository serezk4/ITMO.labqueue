package com.serezka.database.repository.university;

import com.serezka.database.model.university.Flow;
import com.serezka.database.model.university.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlowRepository extends JpaRepository<Flow, Long> {
    boolean existsByName(String name);

    Flow findByName(String name);
    Optional<Flow> findByNameAndSecret(String name, String secret);

    List<Flow> findAllByPeopleContaining(Person person);
}
