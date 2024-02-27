package com.serezka.telegram.bot;

import com.serezka.database.model.telegram.TelegramUser;
import com.serezka.database.service.telegram.TelegramUserService;
import com.serezka.localization.Localization;
import com.serezka.telegram.command.Command;
import com.serezka.telegram.session.step.StepSessionManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Main class for update handling
 *
 * @version 1.0
 */
@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PropertySource("classpath:telegram.properties")
public class Handler {
    @Getter
    List<Command> commands;

    // entities
    TelegramUserService telegamUserService;

    // localization
    Localization localization = Localization.getInstance();

    // cache
    Set<Long> authorized = Collections.newSetFromMap(new WeakHashMap<>());

    /**
     * proceed the update and handling it
     *
     * @param bot    - self
     * @param update - update from client
     */
    public void process(Bot bot, Update update) {
        log.info("new update received: text: '{}' | chatId: {} | messageId: {} | type: {}", update.getText(), update.getChatId(), update.getMessageId(), update.getQueryType());

        if (!authorized.contains(update.getChatId()))
            checkAuth(update);

        // get user
        final TelegramUser telegramUser = getUser(bot, update);
        if (telegramUser == null) return;

        update.setTelegramUser(telegramUser);

        // validate query
        if (!Settings.availableQueryTypes.contains(update.getQueryType())) {
            bot.send(SendMessage.builder()
                    .chatId(update).text(localization.get("handler.query.type_error", telegramUser))
                    .build());
            return;
        }

        // check session

        // todo menu session
        if (update.hasCallbackQuery()) {
            List<String> info = update.getCallbackQuery().getFormatted().info();
        }

        if (StepSessionManager.containsSession(update.getChatId())) {
            Objects.requireNonNull(StepSessionManager.getSession(update.getChatId())).next(bot, update);
            return;
        }

        // get command
        List<Command> filtered = commands.stream()
                .filter(command -> command.getUsage().contains(update.getText()))
                .toList();

        if (filtered.isEmpty()) {
            log.warn("Command not found | {} | {}", update.getText(), telegramUser);
            bot.send(SendMessage.builder()
                    .chatId(update).text(localization.get("handler.command.not_found", telegramUser))
                    .build());

            return;
        }

        if (filtered.size() > 1) log.warn("Multiple commands found | {} | {}", update.getText(), filtered.toString());

        // execute
        filtered.getFirst().execute(bot, update);

        // delete message summon message if needed
        if (telegramUser.isDeleteCommandSummonMessages()) {
            bot.executeAsync(DeleteMessage.builder()
                    .chatId(update.getChatId()).messageId(update.getMessageId())
                    .build());
        }
    }

    /**
     * Get user from database
     *
     * @param bot    - self
     * @param update - update from client
     * @return user from database
     */
    private TelegramUser getUser(Bot bot, Update update) {
        Optional<TelegramUser> optionalUser = telegamUserService.findByChatId(update.getChatId());

        if (optionalUser.isEmpty()) {
            log.warn("User exception (can't find or create) | {} : {}", update.getUsername(), update.getChatId());
            bot.executeAsync(SendMessage.builder()
                    .chatId(update).text(localization.get("handler.database.error"))
                    .build());
            return null;
        }

        authorized.add(update.getChatId());
        return optionalUser.get();
    }

    /**
     * Check user in database
     *
     * @param update
     */
    private void checkAuth(Update update) {
        if (!telegamUserService.existsByChatIdOrUsername(update.getChatId(), update.getUsername()))
            telegamUserService.save(new TelegramUser(update.getChatId(), update.getUsername()));
    }

    /**
     * Get help for user by his role
     *
     * @param TelegramUser - user from database
     * @return help message
     */
    public String getHelp(TelegramUser TelegramUser) {
        return localization.get("help.title", TelegramUser) + "\n" + commands.stream()
                .filter(command -> command.getRequiredRole().getAdminLvl() <= TelegramUser.getRole().getAdminLvl())
                .map(command -> String.format(localization.get("help.command"), command.getUsage(), command.getHelp()))
                .collect(Collectors.joining());
    }
}
