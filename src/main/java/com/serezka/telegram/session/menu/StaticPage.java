package com.serezka.telegram.session.menu;

import org.telegram.telegrambots.meta.api.objects.CallbackBundle;

public class StaticPage implements PageGenerator {
    private final Page page;

    public StaticPage(Page page) {
        this.page = page;
    }

    @Override
    public Page apply(MenuSession menuSession, CallbackBundle callbackBundle) {
        return page;
    }
}
