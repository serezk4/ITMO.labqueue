package com.serezka.database.service.telegram;

import com.serezka.database.model.telegram.TelegramFile;
import com.serezka.database.model.university.Student;
import com.serezka.database.repository.telegram.TelegramFileRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.File;

import java.util.Arrays;
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
    public List<TelegramFile> findAllByStudent(Student student) {
        return telegramFileRepository.findAllByStudent(student);
    }
}
