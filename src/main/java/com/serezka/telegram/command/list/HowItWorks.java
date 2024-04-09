package com.serezka.telegram.command.list;

import com.serezka.database.model.telegram.TelegramUser;
import com.serezka.telegram.bot.Bot;
import com.serezka.telegram.command.Command;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class HowItWorks extends Command {
    public HowItWorks() {
        super(List.of("/how"), "как работает очередь", TelegramUser.Role.MIN);
    }

    @Override
    public void execute(Bot bot, Update update) {
        bot.execute(SendMessage.builder()
                .chatId(update.getChatId())
                .text("""
                        1️⃣ Сначала вы записываетесь в очередь до начала практики.
                        2️⃣ Выбираете желаемую позицию `начало, середина или конец`.
                        3️⃣ Далее, когда начинается практика выбор закрывается и рандомом внутри каждой категории раздаются места.
                        4️⃣ Те, кто не записался в очередь, получают места в конце очереди.
                        5️⃣ Прикрепляете отчет и вы будете вызваны на защиту в порядке очереди.
                        """)
                .build());
    }
}
