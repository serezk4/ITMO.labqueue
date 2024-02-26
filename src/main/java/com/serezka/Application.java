package com.serezka;

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

import java.time.LocalDateTime;
import java.util.TimeZone;

@SpringBootApplication
@RequiredArgsConstructor @Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Application implements ApplicationRunner {
    @Getter
    private static final LocalDateTime startTime = LocalDateTime.now();

    // bot
    Bot bot;

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);

    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);
    }
}
