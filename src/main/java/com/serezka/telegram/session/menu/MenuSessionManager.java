package com.serezka.telegram.session.menu;

import lombok.Synchronized;

import java.util.ArrayList;
import java.util.List;

public class MenuSessionManager {
    private static final List<MenuSession> sessions = new ArrayList<>();

    @Synchronized
    public static boolean containsSession(long chatId) {
        return sessions.stream().anyMatch(session -> session.getChatId() == chatId);
    }

    @Synchronized
    public static MenuSession getSession(long chatId) {
        return sessions.stream().filter(session -> session.getChatId() == chatId).findFirst().orElse(null);
    }

    @Synchronized
    public static void addSession(MenuSession session) {
        sessions.add(session);
    }

    @Synchronized
    public static void removeSession(MenuSession session) {
        sessions.remove(session);
    }

    @Synchronized
    public static void removeSession(long chatId) {
        sessions.removeIf(session -> session.getChatId() == chatId);
    }
}
