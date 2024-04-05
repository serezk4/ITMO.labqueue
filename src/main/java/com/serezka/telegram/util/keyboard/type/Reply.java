package com.serezka.telegram.util.keyboard.type;

import com.serezka.telegram.util.keyboard.Keyboard;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;

import java.util.*;
import java.util.stream.IntStream;

public class Reply {
    public static final ReplyKeyboardMarkup DEFAULT = getDefault();

    private static ReplyKeyboardMarkup getDefault() {
        return getCustomKeyboard(new String[][]{
                {"Зарегистрироваться", "Профиль"},
                {"Добавить поток"},
                {"todo"}
        });
    }

    public static ReplyKeyboardMarkup getCustomKeyboard(List<List<String>> buttonsText) {
        return getCustomKeyboard(buttonsText, false);
    }

    public static ReplyKeyboardMarkup getCustomKeyboard(List<List<String>> buttonsText, boolean addButtons) {
        if (addButtons) buttonsText.add(List.of(Keyboard.Actions.BACK.getName(), Keyboard.Actions.CLOSE.getName()));
        ReplyKeyboardMarkup replyKeyboard = new ReplyKeyboardMarkup(buttonsText.stream()
                .map(row -> new KeyboardRow(
                        row.stream()
                                .filter(Objects::nonNull)
                                .map(Reply::getButton).toList()))
                .toList());

        replyKeyboard.setResizeKeyboard(true);

        return replyKeyboard;
    }

    public static ReplyKeyboardMarkup getCustomKeyboard(String[][] buttonsText) {
        return getCustomKeyboard(Arrays.stream(buttonsText).map(Arrays::asList).toList());
    }

    public static ReplyKeyboardMarkup getResizableKeyboard(List<Button> buttons, int rowSize) {
        List<KeyboardRow> mainRow = new ArrayList<>();
        Queue<Button> buttonsQueue = new ArrayDeque<>(buttons);

        while (!buttonsQueue.isEmpty()) {
            mainRow.add(new KeyboardRow(
                    IntStream.range(0, Math.min(rowSize, buttonsQueue.size()))
                            .mapToObj(i -> buttonsQueue.poll())
                            .filter(Objects::nonNull)
                            .map(Reply::getButton)
                            .toList()
            ));
        }

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(mainRow);
        replyKeyboardMarkup.setResizeKeyboard(true);

        return replyKeyboardMarkup;
    }

    private static KeyboardButton getButton(Button button) {
        return getButton(button.getText(), button.getWebAppInfo());
    }

    private static KeyboardButton getButton(String text) {
        return new KeyboardButton(text);
    }

    private static KeyboardButton getButton(String text, WebAppInfo webAppInfo) {
        KeyboardButton tempButton = new KeyboardButton();
        tempButton.setText(text);
        tempButton.setWebApp(webAppInfo);

        return tempButton;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @Getter
    public static class Button {
        WebAppInfo webAppInfo;
        String text;

        public Button(String text, WebAppInfo webAppInfo) {
            this.webAppInfo = webAppInfo;
            this.text = text;
        }

        public Button(String text) {
            this.text = text;
            this.webAppInfo = null;
        }
    }
}
