package com.serezka.telegram.command.list;

import com.serezka.database.model.university.Flow;
import com.serezka.database.model.university.Student;
import com.serezka.database.service.university.FlowService;
import com.serezka.database.service.university.StudentService;
import com.serezka.telegram.bot.Bot;
import com.serezka.telegram.command.SystemCommand;
import com.serezka.telegram.session.step.StepSessionConfiguration;
import com.serezka.telegram.util.keyboard.Keyboard;
import com.serezka.telegram.util.keyboard.type.Inline;
import com.serezka.telegram.util.keyboard.type.Reply;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackBundle;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddFlow extends SystemCommand {
    FlowService flowService;
    StudentService studentService;

    public AddFlow(FlowService flowService, StudentService studentService) {
        super(List.of("/addflow", "Добавить поток"), "добавить поток");

        this.flowService = flowService;
        this.studentService = studentService;
    }

    @Override
    public void execute(Bot bot, Update update) {
        bot.createStepSession(StepSessionConfiguration.create()
                .saveUsersMessages(false).saveBotsMessages(true)
                .execute((s, u) -> {
                    s.send("*Введите название потока:* ..."/*,
                            Reply.getResizableKeyboard(List.of(new Reply.Button("Отмена")), 2)*/);
                })
                .execute((s, u) -> {
                    if (u.getText().equals("Отмена")) {
                        s.send("*Вы отменили добавление потока*", true);
                        s.destroy();
                        return;
                    }

                    if (!flowService.existsByName(u.getText())) {
                        s.append(String.format("`потока %s не существует!`", u.getText())/*,
                                Reply.getResizableKeyboard(List.of(new Reply.Button("Отмена")), 2)*/);
                        s.rollback();
                        return;
                    }

                    Optional<Student> student = studentService.findByTelegramUser(u.getTelegramUser());

                    if (student.isEmpty()) {
                        s.send("*Кажется, вы еще не зарегистрировались в боте.*\n/register - для регистрации\n`Если вы регистрировались, то напишите @serezkk`");
                        s.destroy();
                        return;
                    }

                    Flow selectedFlow = flowService.findByName(u.getText());

                    if (selectedFlow.getStudents().stream().anyMatch(temp -> temp.getId().compareTo(student.get().getId()) == 0)) {
                        s.send("*Вы уже состоите в потоке* " + selectedFlow.getName());
                        s.destroy();
                        return;
                    }

                    selectedFlow.getStudents().add(student.get());
                    flowService.save(selectedFlow);

                    s.send(String.format("*Вы успешно добавили поток* %s%n/myflows - все ваши потоки", selectedFlow.getName()));

                }), update);
    }
}
