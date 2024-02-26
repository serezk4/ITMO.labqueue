package com.serezka;

import com.serezka.database.model.telegram.TelegramUser;
import com.serezka.database.model.university.Practice;
import com.serezka.database.model.university.Teacher;
import com.serezka.database.repository.university.PracticeRepository;
import com.serezka.database.service.university.PracticeService;
import com.serezka.telegram.bot.Bot;
import com.serezka.telegram.bot.Handler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.time.*;
import java.util.TimeZone;

@SpringBootApplication
@RequiredArgsConstructor
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Application implements ApplicationRunner {
    public static final LocalDateTime startTime = LocalDateTime.now();
    public static final ZoneId defaultTimeZone =  ZoneId.of("Europe/Moscow");

    // bot
    Bot bot;

    PracticeService practiceService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        practiceService.save(Practice.builder()
                .date(LocalDate.now())
                .begin(ZonedDateTime.of(LocalDateTime.of(2024, Month.FEBRUARY, 26, 23, 55), defaultTimeZone))
                .build());

        System.out.println(practiceService.findPracticesByDateAndTimeRange(
                LocalDate.now(),
                ZonedDateTime.now(),
                ZonedDateTime.of(LocalDateTime.of(2024, Month.FEBRUARY, 26, 23, 57), defaultTimeZone)
        ));

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);
    }
}
