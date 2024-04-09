package com.serezka.telegram.command.list;

import com.serezka.database.model.telegram.TelegramUser;
import com.serezka.database.model.university.Flow;
import com.serezka.database.service.university.FlowService;
import com.serezka.database.service.university.PersonService;
import com.serezka.telegram.bot.Bot;
import com.serezka.telegram.command.Command;
import com.serezka.telegram.session.menu.Page;
import com.serezka.telegram.session.menu.PageGenerator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MyFlows extends Command {
    FlowService flowService;
    PersonService personService;

    public MyFlows(FlowService flowService, PersonService personService) {
        super(List.of("/myflows"), "подписанные потоки", TelegramUser.Role.MIN);

        this.flowService = flowService;
        this.personService = personService;
    }

    @Override
    public void execute(Bot bot, Update update) {
        if (!personService.existsByTelegramUser(update.getTelegramUser())) {
            bot.execute(SendMessage.builder()
                    .text("Вы не зарегистрированы в системе")
                    .chatId(update)
                    .build());
            return;
        }

        List<Flow> flows = flowService.findAllByStudentsContaining(personService.findByTelegramUser(update.getTelegramUser()).get());

        Map<String, PageGenerator> flowsPage = new HashMap<>();
        flows.forEach(flow -> {
            flowsPage.put(flow.getName(), (menuSession, callbackBundle) -> {
                if (callbackBundle.data().contains("delete")) {
                    flow.getPeople().remove(personService.findByTelegramUser(update.getTelegramUser()).get());
                    flowService.save(flow);

                    return new Page("Поток " + flow.getName() + " удален")
                            .addButtonWithLink("<", "root");
                }
                return new Page(flow.getId().toString())
                        .addButton("Вернуться", "", "root")
                        .addButton("Удалить", "delete", String.valueOf(flow.getName()))
                        .setRowSize(2);
            });
        });

        PageGenerator rootPage = (menuSession, callbackBundle) -> {
            Page page = new Page("*Управление потоками*")
                    .addButtonWithLink("Добавить", "add")
                    .addButtonWithLink("Закрыть", "close")
                    .setRowSize(2);

            // todo fix
            flows.forEach(flow -> page.addButton(flow.getName(), "", String.valueOf(flow.getName())));

            return page;
        };

        bot.createMenuSession(rootPage, flowsPage, update.getChatId());

    }
}
