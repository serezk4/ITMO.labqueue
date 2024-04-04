package com.serezka.telegram.command.list;

import com.serezka.database.model.telegram.TelegramFile;
import com.serezka.database.model.telegram.TelegramUser;
import com.serezka.database.service.telegram.TelegramFileService;
import com.serezka.database.service.university.StudentService;
import com.serezka.telegram.bot.Bot;
import com.serezka.telegram.command.Command;
import com.serezka.telegram.session.step.StepSessionConfiguration;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UploadFile extends Command {
    TelegramFileService telegramFileService;
    StudentService studentService;

    public UploadFile(TelegramFileService telegramFileService, StudentService studentService) {
        super(List.of("/upload"), "загрузить файл в бота", TelegramUser.Role.MIN);
        this.telegramFileService = telegramFileService;
        this.studentService = studentService;
    }

    @Override
    public void execute(Bot bot, Update update) {
        bot.createStepSession(
                StepSessionConfiguration.create()
                        .execute((s, u) -> s.send("*Отправьте файл*"))
                        .execute((s, u) -> {
                            if (u.getMessage().getDocument() != null) {
                                s.append(" `Загрузка...`");

                                try {
                                    Document document = u.getMessage().getDocument();

                                    GetFile getFile = new GetFile();
                                    getFile.setFileId(document.getFileId());

                                    System.out.println(u.getMessage().getDocument());

                                    String filePath = bot.execute(getFile).getFilePath();
                                    File downloaded = bot.downloadFile(filePath,
                                            Files.createTempFile(document.getFileName().split("\\.", 2)[0],
                                                    "." + document.getFileName().split("\\.", 2)[1]).toFile());

                                    TelegramFile telegramFile = telegramFileService.save(TelegramFile.builder()
                                            .name(downloaded.getName())
                                            .content(Files.readAllBytes(downloaded.toPath()))
                                            .student(studentService.findByTelegramUser(u.getTelegramUser()).orElseThrow(() -> new RuntimeException("Student not found")))
                                            .build());

                                    s.append("\n*Файл успешно загружен!*\n" + telegramFile.getId() + telegramFile.getName());
                                } catch (Exception e) {
                                    s.append("\nОшибка загрузки файла: " + e.getMessage());
                                    e.printStackTrace();
                                }

                                // telegramFileService.save(new TelegramFile(u.getMessage().getDocument()));
                            } else {
                                s.append("\nФайл не найден!");
                            }
                        })

                , update);
    }
}
