package com.serezka.telegram.command.list;

import com.serezka.database.model.telegram.TelegramUser;
import com.serezka.telegram.bot.Bot;
import com.serezka.telegram.command.Command;
import com.serezka.telegram.session.menu.MenuSession;
import com.serezka.telegram.session.menu.Page;
import com.serezka.telegram.session.menu.PageGenerator;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;

@Component
public class Start extends Command {
    public Start() {
        super(List.of("/start"), "load bot", TelegramUser.Role.MIN);
    }

    @Override
    public void execute(Bot bot, Update update) {
//        bot.send(SendMessage.builder()
//                .chatId(update).text("Привет! Это бот для записи в очередь для практики ОПД 1.9.\nИспользуй /register для добавления в бота")
//                .build());

        bot.createMenuSession((session, callback) -> new Page("test").addButtonWithLink("test", "test"),
                Map.of("test", (session, callback) -> new Page("test").addButton("qwe")), update.getChatId());
    }
}
