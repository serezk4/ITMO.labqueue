package com.serezka.telegram.command.list.student;

import com.serezka.database.model.university.Flow;
import com.serezka.database.model.university.Person;
import com.serezka.database.service.university.FlowService;
import com.serezka.database.service.university.PersonService;
import com.serezka.telegram.bot.Bot;
import com.serezka.telegram.command.SystemCommand;
import com.serezka.telegram.session.step.StepSessionConfiguration;
import com.serezka.telegram.util.keyboard.type.Inline;
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
    PersonService personService;

    public AddFlow(FlowService flowService, PersonService personService) {
        super(List.of("/addflow", "Добавить поток"), "добавить поток");

        this.flowService = flowService;
        this.personService = personService;
    }

    @Override
    public void execute(Bot bot, Update update) {
        bot.createStepSession(StepSessionConfiguration.create()
                .saveUsersMessages(false).saveBotsMessages(false)
                .execute((s, u) -> {
                    s.send("*Введите название потока:* ...",
                            Inline.getResizableKeyboard(List.of(new Inline.Button("Отменить операцию", CallbackBundle.fromData(List.of("cancel")))), 2));
                })
                .execute((s, u) -> {
                    if (u.getText().equals("Отмена") || (u.hasCallbackQuery() && CallbackBundle.fromCallback(u.getText()).data().stream().anyMatch(q -> q.equals("cancel")))) {
                        s.send("*Вы отменили добавление потока*", false);
                        s.destroy();
                        return;
                    }

                    if (!flowService.existsByName(u.getText())) {
                        s.append(String.format("`потока %s не существует!`", u.getText()),
                                Inline.getResizableKeyboard(List.of(new Inline.Button("Отменить операцию", CallbackBundle.fromData(List.of("cancel")))), 2));
                        s.rollback();
                        return;
                    }

                    s.getStorage().put("flowName", u.getText());

                    s.append(u.getText() + "\n*Введите ключ входа:* ...",
                            Inline.getResizableKeyboard(List.of(new Inline.Button("Отменить операцию", CallbackBundle.fromData(List.of("cancel")))), 2));
                })
                .execute((s, u) -> {
                    if (u.getText().equals("Отмена") || (u.hasCallbackQuery() && CallbackBundle.fromCallback(u.getText()).data().stream().anyMatch(q -> q.equals("cancel")))) {
                        s.send("*Вы отменили добавление потока*", false);
                        s.destroy();
                        return;
                    }

                    if (!u.hasMessage() || !u.getMessage().hasText()) {
                        s.send("*Ошибка, попробуйте ввести поток еще раз*", false);
                        s.rollback(2);
                        return;
                    }

                    String flowName = (String) s.getStorage().get("flowName");
                    String secretKey = u.getText();

                    Optional<Person> student = personService.findByTelegramUser(u.getTelegramUser());

                    if (student.isEmpty()) {
                        s.send("*Кажется, вы еще не зарегистрировались в боте.*\n/register - для регистрации\n`Если вы регистрировались, то напишите @serezkk`");
                        s.destroy();
                        return;
                    }

                    Optional<Flow> optionalFlow = flowService.findByNameAndSecret(flowName, secretKey);

                    if (optionalFlow.isEmpty()) {
                        s.send("Неверный ключ доступа. Попробуйте добавить поток еще раз: /addflow", false);
                        s.destroy();
                        return;
                    }

                    Flow selectedFlow = optionalFlow.get();

                    if (selectedFlow.getPeople().stream().anyMatch(temp -> temp.getId().compareTo(student.get().getId()) == 0)) {
                        s.send("*Вы уже состоите в потоке* " + selectedFlow.getName());
                        s.destroy();
                        return;
                    }

                    selectedFlow.getPeople().add(student.get());
                    flowService.save(selectedFlow);

                    s.send(String.format("*Вы успешно добавили поток* %s%n/myflows - все ваши потоки", selectedFlow.getName()));

                }), update);
    }
}
