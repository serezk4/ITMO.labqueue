package com.serezka.database.service.telegram;

import com.serezka.database.model.telegram.TelegramFile;
import com.serezka.database.model.university.Person;
import com.serezka.database.repository.telegram.TelegramFileRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class TelegramFileService {
    TelegramFileRepository telegramFileRepository;

    @Transactional
    public TelegramFile save(TelegramFile file) {
        return telegramFileRepository.save(file);
    }

    @Transactional
    public List<TelegramFile> findAllByStudent(Person person) {
        return telegramFileRepository.findAllByPerson(person);
    }
}
