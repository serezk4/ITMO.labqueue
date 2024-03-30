package com.serezka.telegram.session.menu;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.CallbackBundle;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.BiFunction;

public interface PageGenerator extends BiFunction<MenuSession, CallbackBundle, Page> {

}
