package com.serezka.telegram.session.menu;

import com.serezka.telegram.bot.Bot;
import com.serezka.telegram.session.Session;
import com.serezka.telegram.util.keyboard.Keyboard;
import com.serezka.telegram.util.keyboard.type.Inline;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackBundle;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;

@Log4j2
@Getter
public class MenuSession implements Session {
    private static long idCounter = 0;
    private final long id = idCounter++;

    private final long chatId;
    private PageGenerator root;
    private Map<String,PageGenerator> pages;

    public MenuSession(long chatId, PageGenerator root, Map<String,PageGenerator> pages) {
        this.chatId = chatId;
        this.root = root;
        this.pages = pages;
    }

    // {menuId};{pageId};{buttonId}

    @SneakyThrows
    public void init(Bot bot, Update update) {
        Page page = root.apply(this, CallbackBundle.empty());
        page.getButtons().forEach(button -> button.getCallbackBundle().link().addFirst(String.valueOf(id)));

        bot.execute(SendMessage.builder()
                .chatId(update.getChatId())
                .text(page.getText())
                .replyMarkup(Inline.getResizableKeyboard(page.getButtons(), page.getRowSize()))
                .build());
    }

    public void next(Bot bot, Update update) {
        if (!update.hasCallbackQuery()) return;

        CallbackBundle callbackBundle = CallbackBundle.fromCallback(update.getCallbackQuery().getData());
        if (callbackBundle.link().size() < 2) return;

        PageGenerator pageGenerator = pages.getOrDefault(callbackBundle.link().getLast(), null);
        if (pageGenerator == null) {
            log.warn("Page with name {} not found", callbackBundle.link().getLast());
            return;
        }

        Page page = pageGenerator.apply(this, callbackBundle);
        page.getButtons().forEach(button -> button.getCallbackBundle().link().addFirst(String.valueOf(id)));

        bot.executeAsync(EditMessageText.builder()
                .chatId(update.getChatId()).messageId(update.getMessageId())
                .text(page.getText())
                .replyMarkup(Inline.getResizableKeyboard(page.getButtons(), page.getRowSize()))
                .build());
    }

}
