package com.serezka.telegram.command.list;

import com.serezka.database.model.telegram.TelegramUser;
import com.serezka.telegram.bot.Bot;
import com.serezka.telegram.command.Command;
import com.serezka.telegram.session.step.StepSessionConfiguration;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class Register extends Command {
    public Register() {
        super(List.of("register"), "зарегистрироваться в боте", TelegramUser.Role.MIN);
    }

    @Override
    public void execute(Bot bot, Update update) {
        bot.createStepSession(StepSessionConfiguration.create()
                .saveUsersMessages(false)
                .execute((session, request) -> session.send("*Введите данные:*\n*Группа:* "))
                .execute((session, request) -> session.append(request.getText() + "\n*Поток:* "))
                .execute((session, request) -> session.append(request.getText() + "\n*ФИО:* "))
                .execute((session, request) -> session.append(request.getText() + "\n\n*Вы уверены в правильности данных?* (да/нет)"))
                .execute((session, request) -> {
                    if (!request.getText().equalsIgnoreCase("да")) {
                        session.send("Регистрация отменена\n" + getUsage().getFirst() + " для повторной регистрации");
                        return;
                    }


                }), update);
    }
}
