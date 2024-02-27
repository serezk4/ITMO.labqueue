package com.serezka.database.repository.university;

import com.serezka.database.model.telegram.TelegramUser;
import com.serezka.database.model.university.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByIsuId(Long isuId);

    List<Student> findAllByName(String name);

    Optional<Student> findByTelegramUser(TelegramUser telegramUser);

    boolean existsByTelegramUser(TelegramUser telegramUser);
}
