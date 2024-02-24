package com.serezka.telegram.session.menu;

import com.serezka.telegram.bot.Bot;
import com.serezka.telegram.util.keyboard.Keyboard;
import com.serezka.telegram.util.keyboard.type.Inline;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Triple;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackBundle;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.*;

/**
 * Menu session
 *
 * @version 1.0
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public class MenuSession {
    // configuration
    MenuSessionConfiguration configuration;

    // chat data
    Bot bot;
    long chatId;

    // runtime data
    Deque<Message> botMessages = new ArrayDeque<>();
    long id = Math.abs(UUID.randomUUID().toString().hashCode());

    // cache
    @NonFinal
    Map<Long, Menu> cachedMenus;

    public MenuSession(MenuSessionConfiguration configuration, Bot bot, long chatId) {
        this.configuration = configuration;
        this.bot = bot;
        this.chatId = chatId;
    }

    public void next(Bot bot, Update update) {
        // init
        if (botMessages.isEmpty()) {
            Menu menu = configuration.getMenus().get(configuration.getInitLink());
            PageResponse pageResponse = menu.apply(CallbackBundle.empty(),this);
            pageResponse.mapButtons(id);

            send(pageResponse.text(), Inline.getResizableKeyboard(pageResponse.buttons(), pageResponse.columns()));

            return;
        }

        if (!update.hasCallbackQuery()) {
            log.warn("update has no callback query");
            return;
        }

        final String callback = update.getCallbackQuery().getData();
        final String[] args = callback.split("\\" + Keyboard.Delimiter.SERVICE, 2);

        if (args.length < 2) {
            log.warn("callback has no service delimiter, can't parse button ID");
            return;
        }

        if (!args[0].matches("\\d+")) {
            log.warn("callback has no valid button or session ID {}", Arrays.toString(args));
            return;
        }

        Menu selected = configuration.getMenus().get(args[1]);
        List<String> menuArgs = args.length > 2 ? Arrays.stream(args[2].split("\\" + Keyboard.Delimiter.DATA)).toList() : Collections.emptyList();

    }

    public void send(String text, ReplyKeyboard replyKeyboard) {
        // todo make check reply keyboard
        if (botMessages.peekLast() != null && botMessages.peekLast().getMessageId() != null) {
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
                    message.setText(sendMessage.getText());
                    botMessages.add(message);
                    return;
                }
            }
        });
    }
}
