package com.serezka.configuration.telegram.bot;

import com.serezka.database.service.telegram.MessageService;
import com.serezka.telegram.bot.Bot;
import com.serezka.telegram.bot.Handler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:telegram.properties")
public class BotConfiguration {
    @Bean
    public Bot bot(@Value("${telegram.bot.username}") String botUsername,
                   @Value("${telegram.bot.token}") String botToken,
                   @Value("${telegram.bot.threads}") int threadCount,
                   MessageService messageService, Handler handler) {
        return new Bot(botUsername, botToken, threadCount, messageService, handler);
    }
}
