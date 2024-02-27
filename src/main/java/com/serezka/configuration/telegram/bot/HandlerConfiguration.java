package com.serezka.configuration.telegram.bot;

import com.serezka.database.service.telegram.TelegramUserService;
import com.serezka.telegram.bot.Handler;
import com.serezka.telegram.command.Command;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Configuration
@ComponentScan("com.serezka.telegram.command.list")
@PropertySource("classpath:telegram.properties")
public class HandlerConfiguration {
    @Bean
    public Handler handler(List<Command> commands, TelegramUserService telegamUserService) {
        return new Handler(commands, telegamUserService);
    }
}
