package com.serezka.telegram.command.list;

import com.serezka.database.model.telegram.TelegramUser;
import com.serezka.database.model.university.Person;
import com.serezka.database.service.university.StudentService;
import com.serezka.telegram.bot.Bot;
import com.serezka.telegram.command.Command;
import com.serezka.telegram.session.step.StepSessionConfiguration;
import com.serezka.telegram.util.keyboard.type.Inline;
import com.serezka.telegram.util.keyboard.type.Reply;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackBundle;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Register extends Command {
    StudentService studentService;

    public Register(StudentService studentService) {
        super(List.of("/register", "/start", "Зарегистрироваться"), "load bot", TelegramUser.Role.MIN);
        this.studentService = studentService;
    }

    @Override
    public void execute(Bot bot, Update update) {
        if (studentService.findByTelegramUser(update.getTelegramUser()).isPresent()) {
            bot.execute(SendMessage.builder()
                    .chatId(update.getChatId()).text("*Вы уже зарегистрированы!*")
                    .build());
            return;
        }

        bot.createStepSession(StepSessionConfiguration.create()
                        .saveUsersMessages(false)
                        .execute((s, u) -> s.send("*Выберите свою роль:*", Inline.getResizableKeyboard(
                                List.of(
                                        new Inline.Button("Преподаватель", CallbackBundle.fromData(List.of(Person.Role.TEACHER.name()))),
                                        new Inline.Button("Студент", CallbackBundle.fromData(List.of(Person.Role.STUDENT.name())))
                                ), 2)))
                        .execute((s, u) -> {
                            if (!u.hasCallbackQuery()) {
                                s.rollback();
                                return;
                            }

                            CallbackBundle callbackBundle = CallbackBundle.fromCallback(u.getText());

                            if (callbackBundle.data().isEmpty()) {
                                s.append("` Ошибка: пустое значение роли!`");
                                s.rollback();
                                return;
                            }

                            if (Arrays.stream(Person.Role.values()).noneMatch(role -> role.name().equals(callbackBundle.data().getFirst()))) {
                                s.append("` Ошибка: неверное значение роли!`");
                                s.rollback();
                                return;
                            }

                            Person.Role selected = Person.Role.valueOf(callbackBundle.data().getFirst());

                            s.getStorage().put("role", selected);
                            s.append(" " + selected.getName() + "\n*ФИО*:");
                        })
                        .execute((s, u) -> {
                            if (!u.hasMessage() || !u.getMessage().hasText()) {
                                s.rollback();
                                return;
                            }

                            s.getStorage().put("name", u.getMessage().getText());
                            s.append(" " + u.getMessage().getText() + "\n*ISU ID:*");
                        })
                        .execute((s, u) -> {
                            if (!u.hasMessage() || !u.getMessage().hasText()) {
                                s.rollback();
                                return;
                            }

                            if (!u.getText().matches("\\d{6}")) {
                                s.append("` неверный формат`");
                                s.rollback();
                                return;
                            }

                            s.getStorage().put("isuId", u.getMessage().getText());
                            s.append(" " + u.getMessage().getText() + "\n\n*Вы точно уверены в введенных данных?*\n`При последующих изменениях учетной записи вас автоматически отпишет от всех потоков.`",
                                    Inline.getResizableKeyboard(
                                            List.of(
                                                    new Inline.Button("Все правильно", CallbackBundle.fromData(List.of("yes"))),
                                                    new Inline.Button("Есть ошибки", CallbackBundle.fromData(List.of("no")))
                                            ), 2));
                        })
//                        .execute((s, u) -> {
//                            if (!u.hasCallbackQuery()) {
//                                s.rollback();
//                                return;
//                            }
//
//                            if (CallbackBundle.fromCallback(u.getText()).data().getFirst().equals("no")) {
//                                s.send("*Регистрация отменена!*", Reply.getResizableKeyboard(List.of(new Reply.Button("Зарегистрироваться")), 1));
//                                StepSessionManager.removeSession(u.getChatId());
//                                return;
//                            }
//
//                            s.append("\n*Точно уверенны?*",
//                                    Inline.getResizableKeyboard(
//                                            List.of(
//                                                    new Inline.Button("Точно все правильно", CallbackBundle.fromData(List.of("yes"))),
//                                                    new Inline.Button("Я ошибся!", CallbackBundle.fromData(List.of("no")))
//                                            ), 2));
//                        })
                        .execute((s, u) -> {
                            if (!u.hasCallbackQuery()) {
                                s.rollback();
                                return;
                            }

                            if (CallbackBundle.fromCallback(u.getText()).data().getFirst().equals("no")) {
                                s.send("*Регистрация отменена!*", Reply.getResizableKeyboard(List.of(new Reply.Button("Зарегистрироваться")), 1));
                                return;
                            }

                            Person.Role role = (Person.Role) s.getStorage().get("role");
                            String name = (String) s.getStorage().get("name");
                            String isuId = (String) s.getStorage().get("isuId");

                            Person person = studentService.save(Person.builder()
                                    .role(role)
                                    .name(name)
                                    .isuId(Long.valueOf(isuId))
                                    .telegramUser(u.getTelegramUser())
                                    .build());

                            s.send("Данные сохранены!");
                        })
                , update);
    }
}
