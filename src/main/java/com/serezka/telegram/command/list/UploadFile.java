package com.serezka.telegram.command.list;

import com.serezka.database.model.telegram.TelegramFile;
import com.serezka.database.model.telegram.TelegramUser;
import com.serezka.database.model.university.Flow;
import com.serezka.database.service.telegram.TelegramFileService;
import com.serezka.database.service.university.FlowService;
import com.serezka.database.service.university.PersonService;
import com.serezka.telegram.bot.Bot;
import com.serezka.telegram.command.Command;
import com.serezka.telegram.session.step.StepSessionConfiguration;
import com.serezka.telegram.util.keyboard.type.Inline;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackBundle;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UploadFile extends Command {
    TelegramFileService telegramFileService;
    PersonService personService;
    FlowService flowService;

    public UploadFile(TelegramFileService telegramFileService, PersonService personService, FlowService flowService) {
        super(List.of("/upload"), "загрузить файл в бота", TelegramUser.Role.MIN);

        this.telegramFileService = telegramFileService;
        this.personService = personService;
        this.flowService = flowService;
    }

    @Override
    public void execute(Bot bot, Update update) {
        bot.createStepSession(
                StepSessionConfiguration.create()
                        .execute((s, u) -> s.send("*Выберите поток*:",
                                Inline.getResizableKeyboard(flowService.findAllByStudentsContaining(personService.findByTelegramUser(u.getTelegramUser()).orElseThrow(() -> new RuntimeException("Student not found")))
                                        .stream()
                                        .map(flow -> new Inline.Button(flow.getName(), CallbackBundle.fromData(List.of(flow.getId().toString()))))
                                        .toList(), 2)))
                        .execute((s, u) -> s.send("*Отправьте файл*"))
                        .execute((s, u) -> {
                            if (u.getMessage().getDocument() == null) {
                                s.append("\nФайл не найден!");
                                return;
                            }

                            s.append(" `Загрузка...`");

                            try {
                                Document document = u.getMessage().getDocument();
                                Optional<Flow> selectedFlow = flowService.findById(Long.valueOf(s.getUserCallbacks().getFirst().data().getFirst()));

                                if (selectedFlow.isEmpty()) {
                                    s.send("\nПоток не найден!");
                                    return;
                                }

                                GetFile getFile = new GetFile();
                                getFile.setFileId(document.getFileId());

                                String filePath = bot.execute(getFile).getFilePath();
                                File downloaded = bot.downloadFile(filePath,
                                        Files.createTempFile(document.getFileName().split("\\.", 2)[0],
                                                "." + document.getFileName().split("\\.", 2)[1]).toFile());

                                TelegramFile telegramFile = telegramFileService.save(TelegramFile.builder()
                                        .name(downloaded.getName())
                                        .content(Files.readAllBytes(downloaded.toPath()))
                                        .flow(selectedFlow.get())
                                        .person(personService.findByTelegramUser(u.getTelegramUser()).orElseThrow(() -> new RuntimeException("Student not found")))
                                        .build());

                                s.send("\n*Файл успешно загружен!*\n*Файл:* `" + telegramFile.getId() + telegramFile.getName() + "`\n*Поток:* " + selectedFlow.get().getName() + "\n\n/myfiles - все ваши файлы");

                                DeleteMessage deleteMessage = DeleteMessage.builder()
                                        .chatId(u.getChatId())
                                        .messageId(u.getMessage().getMessageId())
                                        .build();
                                bot.execute(deleteMessage);
                            } catch (Exception e) {
                                s.send("\nОшибка загрузки файла: " + e.getMessage());
                            }

                        })

                , update);
    }
}
