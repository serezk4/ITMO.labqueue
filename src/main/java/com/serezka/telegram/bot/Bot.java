package com.serezka.telegram.bot;

import com.serezka.localization.Localization;
import com.serezka.telegram.session.menu.MenuSession;
import com.serezka.telegram.session.menu.MenuSessionManager;
import com.serezka.telegram.session.menu.Page;
import com.serezka.telegram.session.menu.PageGenerator;
import com.serezka.telegram.session.step.StepSession;
import com.serezka.telegram.session.step.StepSessionConfiguration;
import com.serezka.telegram.session.step.StepSessionManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Main class for bot
 * Handles updates and transfer to handler
 *
 * @version 1.0
 * @see Handler
 */
@Log4j2
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Bot extends TelegramLongPollingBot {
    /* bot data     */ String botUsername, botToken;
    /* handler      */ Handler handler;
    /* executor     */ ExecutorServiceRouter executor;
    /* localization */ Localization localization = Localization.getInstance();

    public Bot(String botUsername, String botToken, int threadCount,
               Handler handler) {
        super(botToken);

        this.botUsername = botUsername;
        this.botToken = botToken;
        this.handler = handler;

        this.executor = new ExecutorServiceRouter(threadCount);
    }


    /**
     * Method for handling updates
     *
     * @param update Update received
     * @see Update
     */
    @Override
    public void onUpdateReceived(Update update) {
        log.info("new update received");

        if (executor.isShutdown()) {
            log.info("user {} {} trying to make query", update.getUsername(), update.getChatId());
            send(SendMessage.builder()
                    .chatId(update).text(localization.get("bot.shutdown"))
                    .build());
            return;
        }

        executor.route(update.getChatId(), () -> handler.process(this, update));
    }

    public <T extends Serializable, Method extends BotApiMethod<T>> CompletableFuture<T> send(Method method) {
        return executeAsync(method);
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> CompletableFuture<T> executeAsync(Method method) {
        try {
            log.info("executed method: {}", method.getClass().getSimpleName());

            if (method instanceof SendMessage parsed) {
//                if (parsed.getReplyMarkup() == null)
//                    parsed.setReplyMarkup(Keyboard.Reply.DEFAULT); todo (in sessions can't edit with reply markup)

                log.info(String.format("message sent to {%s} with text {'%s'}",
                        parsed.getChatId(), parsed.getText().replaceAll("\n", " ")));

                return (CompletableFuture<T>) super.executeAsync(parsed);
            } else return super.executeAsync(method);
        } catch (TelegramApiException e) {
            log.warn("Error method execution: {}", e.getMessage());
            return null;
        }
    }

    public <T extends Serializable, Method extends BotApiMethod<T>> CompletableFuture<T> executeAsync(Method method, StepSession stepSession) {
        if (method instanceof SendMessage) {
            CompletableFuture<Message> message = executeAsync((SendMessage) method);
            message.thenRun(() -> {
                try {
                    stepSession.getBotMessages().add(message.get());
                } catch (InterruptedException | ExecutionException e) {
                    log.warn(e.getMessage());
                }
            });
            return (CompletableFuture<T>) message;
        } // todo make other Send*

        return executeAsync(method);
    }

    public void createMenuSession(PageGenerator root, Map<String, PageGenerator> pages, long chatId) {
        MenuSession menuSession = new MenuSession(chatId, root, pages);
        MenuSessionManager.addSession(menuSession);
        menuSession.init(this, chatId);
    }

    // todo remove update field from here
    public void createStepSession(StepSessionConfiguration configuration, Update update) {
        StepSession created = new StepSession(configuration, this, update.getChatId());
        StepSessionManager.addSession(update.getChatId(), created);
        created.next(this, update);
    }
}


