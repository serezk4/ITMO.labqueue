package com.serezka.database.repository.university;

import com.serezka.database.model.telegram.TelegramUser;
import com.serezka.database.model.university.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByTelegramUser(TelegramUser telegramUser);
}
