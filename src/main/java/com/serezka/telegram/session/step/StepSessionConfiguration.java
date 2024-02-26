package com.serezka.telegram.session.step;

import com.serezka.telegram.bot.Bot;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class StepSessionConfiguration {
    boolean saveUsersMessages = true, saveBotsMessages = true;
    boolean canEditMessages = true;
    final Deque<Step> steps = new LinkedList<>();

    public StepSessionConfiguration execute(Step step) {
        steps.add(step);
        return this;
    }

    public StepSessionConfiguration saveUsersMessages(boolean val) {
        this.saveUsersMessages = val;
        return this;
    }

    public StepSessionConfiguration saveBotsMessages(boolean val) {
        this.saveBotsMessages = val;
        return this;
    }

    public StepSessionConfiguration canEditMessages(boolean val) {
        this.canEditMessages = val;
        return this;
    }

    public StepSessionConfiguration get(String text, ReplyKeyboard replyKeyboard) {
        steps.add((session, update) -> session.send(SendMessage.builder()
                .chatId(update)
                .text(text).replyMarkup(replyKeyboard)
                .build())
        );
        return this;
    }

    public static StepSessionConfiguration create() {
        return new StepSessionConfiguration();
    }
}
