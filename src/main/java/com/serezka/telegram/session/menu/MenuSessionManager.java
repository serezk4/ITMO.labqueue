package com.serezka.telegram.session.menu;

import lombok.extern.log4j.Log4j2;

import java.util.*;

@Log4j2
public class MenuSessionManager {
    private static final Map<Long, List<MenuSession>> menuSessions = new HashMap<>();

    public static boolean containsSession(long chatId, long sessionId) {
        if (!menuSessions.containsKey(chatId)) return false;

        log.info("Checking session in chat " + chatId);

        synchronized (menuSessions.get(chatId)) {
            return menuSessions.get(chatId).stream().anyMatch(session -> session.getId() == sessionId);
        }
    }

    public static MenuSession getSession(long chatId, long sessionId) {
        if (!containsSession(chatId, sessionId)) return null;

        log.info("Getting session from chat " + chatId);

        synchronized (menuSessions.get(chatId)) {
            return menuSessions.get(chatId).stream().filter(session -> session.getId() == sessionId).findFirst().orElse(null);
        }
    }

    public static void addSession(long chatId, MenuSession menuSession) {
        if (!containsSession(chatId, menuSession.getId())) menuSessions.put(chatId, new ArrayList<>());

        log.info("Adding session " + menuSession.getId() + " to chat " + chatId);

        synchronized (menuSessions.get(chatId)) {
            menuSessions.get(chatId).add(menuSession);
        }
    }

    public static void removeSession(long chatId, MenuSession menuSession) {
        if (!containsSession(chatId, menuSession.getId())) return;

        log.info("Removing session " + menuSession.getId() + " from chat " + chatId);

        synchronized (menuSessions.get(chatId)) {
            menuSessions.get(chatId).remove(menuSession);
        }
    }

    public static void removeSession(long chatId) {
//        if (!containsSession(chatId)) return;

        log.info("Removing session from chat " + chatId);

        synchronized (menuSessions.get(chatId)) {
            menuSessions.get(chatId).clear();
        }
    }
}
