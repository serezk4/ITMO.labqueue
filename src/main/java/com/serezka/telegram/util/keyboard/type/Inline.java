package com.serezka.telegram.util.keyboard.type;

import com.serezka.telegram.util.keyboard.Keyboard;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import okhttp3.Call;
import org.telegram.telegrambots.meta.api.objects.CallbackBundle;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;

import java.util.*;
import java.util.stream.IntStream;

public class Inline {
    public static InlineKeyboardMarkup getStaticKeyboard(Button[][] buttonsData) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        Arrays.stream(buttonsData).forEach(row ->
                rows.add(Arrays.stream(row).
                        filter(Objects::nonNull).
                        map(Inline::getButton).
                        toList()
                )
        );

        return new InlineKeyboardMarkup(rows);
    }

    public static InlineKeyboardMarkup getResizableKeyboard(List<? extends Button> buttonsData, int rowSize) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        Queue<Button> buttonsQueue = new PriorityQueue<>(buttonsData);

        while (!buttonsQueue.isEmpty())
            rows.add(
                    IntStream.range(0, Math.min(buttonsQueue.size(), rowSize))
                            .mapToObj(i -> buttonsQueue.poll())
                            .filter(Objects::nonNull)
                            .map(Inline::getButton)
                            .toList()
            );

        return new InlineKeyboardMarkup(rows);
    }

    private static InlineKeyboardButton getButton(String text, CallbackBundle callbackBundle) {
        InlineKeyboardButton tempInlineButton = new InlineKeyboardButton(text);
//        tempInlineButton.setCallbackData(id + Keyboard.Delimiter.SERVICE + callbackData);

        return tempInlineButton;
    }

    private static InlineKeyboardButton getButton(Button button) {
//        return button.getWebAppInfo() != null ? todo
//                getButton(button.getText(), button.getWebAppInfo(), button.getSessionId()) :
//                getButton(button.getText(), String.join(Keyboard.Delimiter.SERVICE, button.getData()), button.getSessionId());

        return getButton(button.getText(), new CallbackBundle(List.of("todo"), List.of("test")));
    }

    private static InlineKeyboardButton getButton(String text, WebAppInfo webAppInfo, long id) {
        InlineKeyboardButton tempInlineButton = new InlineKeyboardButton(text);
        tempInlineButton.setWebApp(webAppInfo);

        return tempInlineButton;
    }

    @Getter @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Builder @AllArgsConstructor
    public static class Button {
        private static long counter = 0;
        String text;
        CallbackBundle callbackBundle;

        WebAppInfo webAppInfo;

        public Button(String text, CallbackBundle callbackBundle) {
            this.text = text;
            this.callbackBundle = callbackBundle;
            this.webAppInfo = null;
        }

        public Button(String text, WebAppInfo webAppInfo, CallbackBundle callbackBundle) {
            this.text = text;
            this.callbackBundle = callbackBundle;
            this.webAppInfo = webAppInfo;
        }
    }
}
