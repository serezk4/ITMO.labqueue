package com.serezka.database.service.university;

import com.serezka.database.model.telegram.TelegramUser;
import com.serezka.database.model.university.Person;
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
    public Person save(Person person) {
        return studentRepository.save(person);
    }

    @Transactional
    public void removeById(Long id) {
        studentRepository.deleteById(id);
    }

    @Transactional
    public Optional<Person> findById(Long id) {
        return studentRepository.findById(id);
    }

    @Transactional
    public Optional<Person> findByTelegramUser(TelegramUser telegramUser) {
        return studentRepository.findByTelegramUser(telegramUser);
    }

    @Transactional
    public List<Person> findAllByName(String name) {
        return studentRepository.findAllByName(name);
    }

    @Transactional
    public Optional<Person> findByIsuId(Long isuId) {
        return studentRepository.findByIsuId(isuId);
    }

    @Transactional
    public boolean existsByTelegramUser(TelegramUser telegramUser) {
        return studentRepository.existsByTelegramUser(telegramUser);
    }
}
