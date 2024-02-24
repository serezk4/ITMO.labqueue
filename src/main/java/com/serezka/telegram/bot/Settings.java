package com.serezka.telegram.bot;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class Settings {
    public static final List<Update.QueryType> availableQueryTypes = List.of(Update.QueryType.MESSAGE, Update.QueryType.CALLBACK_QUERY, Update.QueryType.INLINE_QUERY);
}
