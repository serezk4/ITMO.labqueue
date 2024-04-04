package com.serezka.telegram.command.list;

import com.serezka.database.model.telegram.TelegramFile;
import com.serezka.database.model.telegram.TelegramUser;
import com.serezka.database.model.university.Student;
import com.serezka.database.service.telegram.TelegramFileService;
import com.serezka.database.service.university.StudentService;
import com.serezka.telegram.bot.Bot;
import com.serezka.telegram.command.Command;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.ReplyParameters;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaDocument;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MyFiles extends Command {
    TelegramFileService telegramFileService;
    StudentService studentService;

    public MyFiles(TelegramFileService telegramFileService, StudentService studentService) {
        super(List.of("/myfiles"), "Посмотреть загруженные файлы", TelegramUser.Role.MIN);

        this.telegramFileService = telegramFileService;
        this.studentService = studentService;
    }


    @Override
    public void execute(Bot bot, Update update) {
        Optional<Student> optionalStudent = studentService.findByTelegramUser(update.getTelegramUser());
        if (optionalStudent.isEmpty()) {
            bot.send(SendMessage.builder()
                    .text("Вы не зарегистрированы в системе")
                    .chatId(update)
                    .build());
            return;
        }

        List<TelegramFile> files = telegramFileService.findAllByStudent(optionalStudent.get());

        if (files.isEmpty()) {
            bot.send(SendMessage.builder()
                    .text("У вас нет загруженных файлов")
                    .chatId(update)
                    .build());
            return;
        }

        try {
            bot.execute(SendMediaGroup.builder()
                    .medias(files.stream()
                            .map(file -> {
                                InputMedia inputMedia = new InputMediaDocument();
                                inputMedia.setMedia(new ByteArrayInputStream(file.getContent()), file.getName());
                                return inputMedia;
                            })
                            .toList())
                    .chatId(update.getChatId())
                    .build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
