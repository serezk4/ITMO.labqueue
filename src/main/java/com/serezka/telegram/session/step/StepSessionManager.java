package com.serezka.telegram.session.step;

import lombok.extern.log4j.Log4j2;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Log4j2
public class StepSessionManager {
    private static final Map<Long, Deque<StepSession>> stepSessions = new HashMap<>();

    public static boolean containsSession(long chatId) {
        if (!stepSessions.containsKey(chatId)) return false;

        log.info("Checking session in chat " + chatId);

        synchronized (stepSessions.get(chatId)) {
            return !stepSessions.get(chatId).isEmpty();
        }
    }

    public static StepSession getSession(long chatId) {
        if (!containsSession(chatId)) return null;

        log.info("Getting session from chat " + chatId);

        synchronized (stepSessions.get(chatId)) {
            return stepSessions.get(chatId).peekLast();
        }
    }

    public static void addSession(long chatId, StepSession stepSession) {
        if (!containsSession(chatId)) stepSessions.put(chatId, new LinkedList<>());

        log.info("Adding session " + stepSession.getId() + " to chat " + chatId);

        synchronized (stepSessions.get(chatId)) {
            stepSessions.get(chatId).push(stepSession);
        }
    }

    public static void removeSession(long chatId, StepSession stepSession) {
        if (!containsSession(chatId)) return;

        log.info("Removing session " + stepSession.getId() + " from chat " + chatId);

        synchronized (StepSessionManager.stepSessions.get(chatId)) {
            StepSessionManager.stepSessions.get(chatId).remove(stepSession);
        }
    }

    public static void removeSession(long chatId) {
        if (!containsSession(chatId)) return;

        log.info("Removing session from chat " + chatId);

        synchronized (stepSessions.get(chatId)) {
            stepSessions.get(chatId).pollLast();
        }
    }
}
