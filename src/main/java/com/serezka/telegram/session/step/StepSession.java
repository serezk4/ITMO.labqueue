package com.serezka.telegram.session.step;

import com.serezka.telegram.bot.Bot;
import com.serezka.telegram.session.Session;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackBundle;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.*;

/**
 * StepSession class for step-by-step interaction with user
 * @version 1.0
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public class StepSession implements Session {
    StepSessionConfiguration configuration;

    Deque<Step> input;
    Deque<Step> trash;

    Bot bot;
    long chatId;

    // session variables
    Deque<Message> botMessages = new ArrayDeque<>();
    Deque<Message> userMessages = new ArrayDeque<>();
    List<CallbackBundle> userCallbacks = new ArrayList<>();

    List<String> data = new LinkedList<>();

    long id = Math.abs(UUID.fromString(UUID.randomUUID().toString()).getLeastSignificantBits());

    List<String> history = new LinkedList<>();

    public StepSession(StepSessionConfiguration configuration, Bot bot, long chatId) {
        this.configuration = configuration;
        this.bot = bot;
        this.chatId = chatId;

        this.input = configuration.getSteps();
        this.trash = new LinkedList<>();
    }

    /**
     * get next step
     *
     * @param bot    - bot
     * @param update - update
     */
    public void next(Bot bot, Update update) {
        if (!update.hasCallbackQuery())
            userMessages.add(update.getMessage());
        if (update.hasCallbackQuery())
            userCallbacks.add(CallbackBundle.fromCallback(update.getCallbackQuery().getData()));

        data.add(update.getText());

        // get next step
        getNext(bot, update);

        // remove last message from bot
        deleteMessages();
    }

    // execute methods
    public void append(String text, ReplyKeyboard replyKeyboard) {
        if (botMessages.isEmpty() || botMessages.peekLast() == null) send(text, replyKeyboard, false);

        botMessages.getLast().setText(botMessages.getLast().getText()
                .replaceAll("\\.\\.\\.", "").replaceAll("`.+`", "") + text);

        send(botMessages.getLast().getText(), replyKeyboard, false);
    }

    public void append(String text) {
        append(text, null);
    }

    public void send(String text) {
        send(text, false);
    }

    public void send(String text, ReplyKeyboard replyKeyboard) {
        send(text, replyKeyboard, false);
    }

    public void send(String text, boolean likeNew) {
        send(text, null, likeNew);
    }

    public void send(String text, ReplyKeyboard replyKeyboard, boolean likeNew) {
        // todo make check reply keyboard
        if (!likeNew && configuration.isCanEditMessages() && botMessages.peekLast() != null && botMessages.peekLast().getMessageId() != null) {
            send(EditMessageText.builder()
                    .chatId(botMessages.peekLast().getChatId()).messageId(botMessages.peekLast().getMessageId())
                    .text(text)
                    .build());

            if (replyKeyboard instanceof InlineKeyboardMarkup inlineKeyboard) {
                send(EditMessageReplyMarkup.builder()
                        .chatId(chatId).messageId(botMessages.peekLast().getMessageId())
                        .replyMarkup(inlineKeyboard)
                        .build());
            }

            return;
        }

        send(SendMessage.builder()
                .chatId(chatId)
                .text(text).replyMarkup(replyKeyboard)
                .build());
    }

    public void send(BotApiMethod<?> method) {
        bot.send(method).whenComplete((response, throwable) -> {
            if (throwable != null) {
                log.warn(throwable);
                return;
            }

            if (response instanceof Message message) {
                if (method instanceof SendMessage sendMessage) {
                    message.setText(sendMessage.getText()
                            .replaceAll("`\\s+`", ""));
                    botMessages.add(message);
                    return;
                }
            }
        });
    }

    public void rollback() {
        input.addFirst(trash.pollLast());
        data.removeLast();
    }

    /**
     * get next step
     *
     * @param bot    - bot
     * @param update - update
     */
    protected void getNext(Bot bot, Update update) {
        log.info("trying to get next step for session {}", id);

        destroyIfEmpty();

        log.info("Session {} has steps, remain {} | next: {}", id, input.size(), input.peek());

        trash.add(input.pop());
        Objects.requireNonNull(trash.peekLast()).accept(this, update);

        destroyIfEmpty();
    }

    protected void deleteMessages() {
        deleteUserMessages();
        deleteBotMessages();
    }

    protected void deleteUserMessages() {
        if (configuration.isSaveUsersMessages()) return;
        if (userMessages.isEmpty()) return;

        userMessages.stream().filter(userMessage -> !userMessage.isDeleted()).forEach(userMessage -> {
            userMessage.setDeleted(true);
            bot.executeAsync(DeleteMessage.builder()
                    .chatId(userMessage.getChatId()).messageId(userMessage.getMessageId())
                    .build());
        });
    }

    protected void deleteBotMessages() {
        if (configuration.isSaveBotsMessages()) return;
        if (botMessages.isEmpty()) return;

        while (!botMessages.isEmpty()) {
            Message temp = botMessages.pollFirst();

            bot.executeAsync(DeleteMessage.builder()
                    .chatId(temp.getChatId()).messageId(temp.getMessageId())
                    .build());
        }

    }

    protected void destroyIfEmpty() {
        if (!input.isEmpty()) return;

        log.info("Session {} has no input, it will be destroyed", id);
        destroy();
    }


    /**
     * execute session
     */
    public void destroy() {
        deleteMessages();

        // remove session from session manager
        StepSessionManager.removeSession(chatId, this);
    }
}
