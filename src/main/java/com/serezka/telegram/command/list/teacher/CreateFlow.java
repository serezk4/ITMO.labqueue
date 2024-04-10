package com.serezka.telegram.command.list.teacher;

import com.serezka.database.model.telegram.TelegramUser;
import com.serezka.database.model.university.Flow;
import com.serezka.database.model.university.Person;
import com.serezka.database.service.university.FlowService;
import com.serezka.database.service.university.PersonService;
import com.serezka.telegram.bot.Bot;
import com.serezka.telegram.command.Command;
import com.serezka.telegram.session.step.StepSessionConfiguration;
import com.serezka.telegram.util.keyboard.type.Inline;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackBundle;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class CreateFlow extends Command {
    FlowService flowService;
    PersonService personService;

    public CreateFlow(FlowService flowService, PersonService personService) {
        super(List.of("/createFlow"), "создать поток", TelegramUser.Role.TEACHER);

        this.flowService = flowService;
        this.personService = personService;
    }

    @Override
    public void execute(Bot bot, Update update) {
        if (!personService.existsByTelegramUser(update.getTelegramUser())) {
            bot.execute(SendMessage.builder()
                    .text("*Вы не зарегистрированы в системе*\nиспользуйте /register")
                    .chatId(update)
                    .build());
            return;
        }

        final Person person = personService.findByTelegramUser(update.getTelegramUser()).get();

        if (person.getRole() != Person.Role.TEACHER) {
            bot.execute(SendMessage.builder()
                    .text("*Вы не являетесь преподавателем*")
                    .chatId(update)
                    .build());
            return;
        }

        bot.createStepSession(StepSessionConfiguration.create()
                        .execute((s, u) -> s.send("*Введите название потока:*",
                                Inline.getResizableKeyboard(List.of(new Inline.Button("Отмена", CallbackBundle.fromData("cancel"))), 1)))
                        .execute((s, u) -> {
                            if (u.hasCallbackQuery() && CallbackBundle.fromCallback(u.getText()).data().stream().anyMatch(q -> q.equals("cancel"))) {
                                s.send("*Вы отменили создание потока*", false);
                                s.destroy();
                                return;
                            }

                            if (flowService.existsByName(u.getText())) {
                                s.append(String.format("`поток %s уже существует!`\n" + getUsage().getFirst() + " - повторить", u.getText()));
                                s.destroy();
                                return;
                            }

                            String accessKey = String.valueOf((int) (Math.random() * 1000));

                            s.append(u.getText() + "\n*Ключ доступа*:" + accessKey);

                            flowService.save(Flow.builder()
                                            .name(u.getText())
                                            .secret(accessKey)
                                            .people(List.of(person))
                                    .build());

                            s.append("\n*Поток успешно создан!*");
                        })
                , update);
    }
}
