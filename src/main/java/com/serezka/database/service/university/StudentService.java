package com.serezka.database.service.university;

import com.serezka.database.model.telegram.TelegramUser;
import com.serezka.database.model.university.Group;
import com.serezka.database.model.university.Student;
import com.serezka.database.repository.university.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StudentService {
    StudentRepository studentRepository;

    @Transactional
    public Student save(Student student) {
        return studentRepository.save(student);
    }

    @Transactional
    public boolean existsByTelegramUser(TelegramUser telegramUser) {
        return studentRepository.existsByTelegramUser(telegramUser);
    }

    @Transactional
    public List<Student> findAllByGroup(Group group) {
        return studentRepository.findAllByGroup(group);
    }
}
