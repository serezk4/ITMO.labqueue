package com.serezka.telegram.command.list;

import com.serezka.database.model.university.Flow;
import com.serezka.database.service.university.FlowService;
import com.serezka.database.service.university.StudentService;
import com.serezka.database.service.university.SubjectService;
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
    SubjectService subjectService;
    FlowService flowService;
    StudentService studentService;

    public Register(SubjectService subjectService, FlowService flowService, StudentService studentService) {
        super(List.of("/register"), "зарегистрироваться в боте");

        this.subjectService = subjectService;
        this.flowService = flowService;
        this.studentService = studentService;
    }

    @Override
    public void execute(Bot bot, Update update) {
        var ref = new Object() {
            Flow flow = null;
        };

        bot.createStepSession(StepSessionConfiguration.create()
                .saveUsersMessages(false)
                .execute((session, request) -> session.send("*Введите данные:*\n*ФИО:* "))
                .execute((session, request) -> session.append(request.getText() + "\n*ИСУ ID:* "))
                .execute((session, request) -> session.append(request.getText() + "\n\n*Вы уверены в правильности данных?* (да/нет)"))
                .execute((session, request) -> {
                    if (!request.getText().equalsIgnoreCase("да")) {
                        session.send("Регистрация отменена\n" + getUsage().getFirst() + " для повторной регистрации");
                        return;
                    }



                }), update);

//        bot.createStepSession(StepSessionConfiguration.create()
//                .saveUsersMessages(false)
//                .execute((session, request) -> session.send("*Введите данные:*\n*Группа:* "))
//                .execute((session, request) -> {
//                    ref.group = groupService.existsByName(request.getText()) ?
//                            groupService.findByName(request.getText()) :
//                            groupService.save(new Group(request.getText()));
//
//                    session.append(String.format("%s%n*Поток:* ", request.getText()));
//                })
//                .execute((session, request) -> {
//                    if (!flowService.existsByName(request.getText())) {
//                        session.append("`Такого потока не существует!`");
//                        session.rollback();
//                        return;
//                    }
//
//                    ref.flow = flowService.findByName(request.getText());
//                    session.append(request.getText() + "\n*ФИО:* ");
//                })
//                .execute((session, request) -> session.append(request.getText() + "\n\n*Вы уверены в правильности данных?* (да/нет)"))
//                .execute((session, request) -> {
//                    if (!request.getText().equalsIgnoreCase("да")) {
//                        session.send("Регистрация отменена\n" + getUsage().getFirst() + " для повторной регистрации");
//                        return;
//                    }
//
//                    // todo
//                    Student student = studentService.save(new Student(session.getData().get(session.getData().size() - 2),
//                            update.getDatabaseUser(), ref.group, Collections.singletonList(ref.flow)));
//                    session.send("*Данные успешно сохранены!*\n" + student.toString());
//                }), update);
    }
}
