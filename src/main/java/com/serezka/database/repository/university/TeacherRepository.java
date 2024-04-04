package com.serezka.database.repository.university;

import com.serezka.database.model.telegram.TelegramUser;
import com.serezka.database.model.university.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Teacher findByName(String name);

    Optional<Teacher> findByTelegramUser(TelegramUser telegramUser);
}
