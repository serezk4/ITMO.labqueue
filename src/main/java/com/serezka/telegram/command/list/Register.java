package com.serezka.telegram.command.list;

import com.serezka.database.model.university.Student;
import com.serezka.database.service.university.StudentService;
import com.serezka.telegram.bot.Bot;
import com.serezka.telegram.command.SystemCommand;
import com.serezka.telegram.session.step.StepSessionConfiguration;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Register extends SystemCommand {
    StudentService studentService;

    public Register(StudentService studentService) {
        super(List.of("/register"), "зарегистрироваться");

        this.studentService = studentService;
    }

    @Override
    public void execute(Bot bot, Update update) {
        bot.createStepSession(StepSessionConfiguration.create()
                .saveUsersMessages(false)
                .execute((s, u) -> {
                    if (studentService.existsByTelegramUser(u.getTelegramUser())) {
                        s.send("*Кажется, вы уже зарегистрированы в боте.*\nНапишите @serezkk для изменеия учетной записи.");
                        s.destroy();
                        return;
                    }

                    s.send("*Заполните данные:*\n*ФИО:* ");
                })
                .execute((s, u) -> {
                    s.append(u.getText() + "\n*ISU ID: *");
                })
                .execute((s, u) -> {
                    if (!u.getText().matches("\\d{6}")) {
                        s.append("`неверный формат`");
                        s.rollback();
                        return;
                    }

                    s.append(u.getText() + "\n\n*Вы уверены в правильности данных? (да/нет)*\n`После подтверждения изменить учетную запись с помощью команды будет невозможно!`");
                })
                .execute((s, u) -> {
                    if (!u.getText().equalsIgnoreCase("да")) {
                        s.send("*Регистрация отменена.*\n" + getUsage().getFirst() + " для повторной регистрации");
                        return;
                    }

                    Student savedStudent = studentService.save(Student.builder()
                            .name(s.getData().get(s.getData().size() - 3))
                            .isuId(Long.parseLong(s.getData().get(s.getData().size() - 2)))
                            .telegramUser(u.getTelegramUser())
                            .build());

                    s.send(String.format("*Вы успешно зарегистрировались в боте!*%n%n*BOT ID:* %d%n*ISU ID:* %d%n*ФИО:* %s",
                            savedStudent.getId(), savedStudent.getIsuId(), savedStudent.getName()));
                }), update);
    }
}
