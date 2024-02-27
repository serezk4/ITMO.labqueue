package com.serezka.database.service.university;

import com.serezka.database.model.telegram.TelegramUser;
import com.serezka.database.model.university.Student;
import com.serezka.database.repository.university.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public void removeById(Long id) {
        studentRepository.deleteById(id);
    }

    @Transactional
    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    @Transactional
    public Optional<Student> findByTelegramUser(TelegramUser telegramUser) {
        return studentRepository.findByTelegramUser(telegramUser);
    }

    @Transactional
    public List<Student> findAllByName(String name) {
        return studentRepository.findAllByName(name);
    }

    @Transactional
    public Optional<Student> findByIsuId(Long isuId) {
        return studentRepository.findByIsuId(isuId);
    }

    @Transactional
    public boolean existsByTelegramUser(TelegramUser telegramUser) {
        return studentRepository.existsByTelegramUser(telegramUser);
    }
}
