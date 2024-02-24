package com.serezka.telegram.session.menu;

import org.telegram.telegrambots.meta.api.objects.CallbackBundle;

import java.util.function.BiFunction;

public interface Menu extends BiFunction<CallbackBundle, MenuSession, PageResponse>{

}
