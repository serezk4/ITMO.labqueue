package com.serezka.telegram.util;

import lombok.AccessLevel;
import lombok.Synchronized;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.WeakHashMap;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component @Log4j2
@PropertySource("classpath:telegram.properties")
public class AntiSpam {
    Long duration;
    Map<Long, Long> usersLastMessage = new WeakHashMap<>();

    private AntiSpam(@Value("${services.anti_spam.duration}") Long duration) {
        this.duration = duration;
    }

    @Synchronized
    public boolean isSpam(long userId) {
        long lastMessageTime = usersLastMessage.getOrDefault(userId, 0L);
        long currentTime = System.currentTimeMillis();

        usersLastMessage.put(userId, currentTime);

        return isWithinAllowedRange(currentTime, lastMessageTime);
    }

    private boolean isWithinAllowedRange(long currentTime, long lastMessageTime) {
        return (currentTime - lastMessageTime) <= duration * 1000;
    }
}
