package com.serezka.database.repository.university;

import com.serezka.database.model.telegram.TelegramUser;
import com.serezka.database.model.university.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByIsuId(Long isuId);

    List<Person> findAllByName(String name);

    Optional<Person> findByTelegramUser(TelegramUser telegramUser);

    boolean existsByTelegramUser(TelegramUser telegramUser);
}
