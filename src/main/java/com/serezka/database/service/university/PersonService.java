package com.serezka.database.service.university;

import com.serezka.database.model.telegram.TelegramUser;
import com.serezka.database.model.university.Person;
import com.serezka.database.repository.university.PersonRepository;
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
public class PersonService {
    PersonRepository personRepository;

    @Transactional
    public Person save(Person person) {
        return personRepository.save(person);
    }

    @Transactional
    public void removeById(Long id) {
        personRepository.deleteById(id);
    }

    @Transactional
    public Optional<Person> findById(Long id) {
        return personRepository.findById(id);
    }

    @Transactional
    public Optional<Person> findByTelegramUser(TelegramUser telegramUser) {
        return personRepository.findByTelegramUser(telegramUser);
    }

    @Transactional
    public List<Person> findAllByName(String name) {
        return personRepository.findAllByName(name);
    }

    @Transactional
    public Optional<Person> findByIsuId(Long isuId) {
        return personRepository.findByIsuId(isuId);
    }

    @Transactional
    public boolean existsByTelegramUser(TelegramUser telegramUser) {
        return personRepository.existsByTelegramUser(telegramUser);
    }
}
