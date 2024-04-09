package com.serezka.telegram.command.list;

import com.serezka.database.model.telegram.TelegramUser;
import com.serezka.database.model.university.Person;
import com.serezka.database.service.university.PracticeService;
import com.serezka.database.service.university.PersonService;
import com.serezka.telegram.bot.Bot;
import com.serezka.telegram.command.Command;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MyProfile extends Command {
    PersonService personService;
    PracticeService practiceService;

    public MyProfile(PersonService personService, PracticeService practiceService) {
        super(List.of("/me", "Профиль"), "посмотреть профиль", TelegramUser.Role.MIN);

        this.personService = personService;
        this.practiceService = practiceService;
    }

    @Override
    public void execute(Bot bot, Update update) {
        TelegramUser user = update.getTelegramUser();

        Optional<Person> person = personService.findByTelegramUser(user);

        final String text = String.format("""
                        <b>Telegram</b>:
                        > <b>имя</b>: %s
                        > <b>роль</b>: %s
                                        
                        <b>Person</b>:
                        > <b>ФИО</b>: %s
                        > <b>роль</b>: %s
                        > <b>ISU ID</b>: %d
                        """, user.getUsername(), user.getRole(),
                person.map(Person::getName).orElse("не указано"),
                person.map(Person::getRole).orElse(null),
                person.map(Person::getIsuId).orElse(-1L));

        bot.execute(SendMessage.builder()
                .text(text)
                .parseMode(ParseMode.HTML)
                .chatId(update)
                .build());
    }
}
