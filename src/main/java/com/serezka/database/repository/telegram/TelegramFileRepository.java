package com.serezka.database.repository.telegram;

import com.serezka.database.model.telegram.TelegramFile;
import com.serezka.database.model.university.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelegramFileRepository extends JpaRepository<TelegramFile, Long> {
    List<TelegramFile> findAllByPerson(Person person);
}
