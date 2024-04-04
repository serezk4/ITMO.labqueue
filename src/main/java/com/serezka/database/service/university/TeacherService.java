package com.serezka.database.service.university;

import com.serezka.database.model.telegram.TelegramUser;
import com.serezka.database.model.university.Teacher;
import com.serezka.database.repository.university.TeacherRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TeacherService {
    TeacherRepository teacherRepository;

    @Transactional
    public Teacher save(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    @Transactional
    public Teacher findByName(String name) {
        return teacherRepository.findByName(name);
    }

    @Transactional
    public Optional<Teacher> findByTelegramUser(TelegramUser telegramUser) {
        return teacherRepository.findByTelegramUser(telegramUser);
    }
}
