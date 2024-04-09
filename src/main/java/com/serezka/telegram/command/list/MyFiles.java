package com.serezka.telegram.command.list;

import com.serezka.database.model.telegram.TelegramFile;
import com.serezka.database.model.telegram.TelegramUser;
import com.serezka.database.model.university.Person;
import com.serezka.database.service.telegram.TelegramFileService;
import com.serezka.database.service.university.PersonService;
import com.serezka.telegram.bot.Bot;
import com.serezka.telegram.command.Command;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaDocument;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public class MyFiles extends Command {
    TelegramFileService telegramFileService;
    PersonService personService;

    public MyFiles(TelegramFileService telegramFileService, PersonService personService) {
        super(List.of("/myfiles"), "Посмотреть загруженные файлы", TelegramUser.Role.MIN);

        this.telegramFileService = telegramFileService;
        this.personService = personService;
    }


    @Override
    public void execute(Bot bot, Update update) {
        Optional<Person> optionalStudent = personService.findByTelegramUser(update.getTelegramUser());
        if (optionalStudent.isEmpty()) {
            bot.send(SendMessage.builder()
                    .text("Вы не зарегистрированы в системе")
                    .chatId(update)
                    .build());
            return;
        }

        List<TelegramFile> files = new ArrayList<>(telegramFileService.findAllByStudent(optionalStudent.get()));

        if (files.isEmpty()) {
            bot.send(SendMessage.builder()
                    .text("У вас нет загруженных файлов")
                    .chatId(update)
                    .build());
            return;
        }

        try {
            while (!files.isEmpty()) {
                if (files.size() == 1) {
                    InputFile inputMedia = new InputFile();
                    inputMedia.setMedia(new ByteArrayInputStream(files.getFirst().getContent()), files.getFirst().getName());

                    bot.execute(SendDocument.builder()
                            .document(inputMedia)
                            .chatId(update.getChatId())
                            .build());

                    files.remove(files.getFirst());
                    continue;
                }

                bot.execute(SendMediaGroup.builder()
                        .medias(files.stream().limit(10)
                                .map(file -> {
                                    InputMedia inputMedia = new InputMediaDocument();
                                    inputMedia.setMedia(new ByteArrayInputStream(file.getContent()), file.getName());
                                    return inputMedia;
                                })
                                .toList())
                        .chatId(update.getChatId())
                        .build());

                files.removeAll(files.stream().limit(10).toList());
            }
        } catch (TelegramApiException e) {
            log.warn(e.getMessage());
        }
    }
}
