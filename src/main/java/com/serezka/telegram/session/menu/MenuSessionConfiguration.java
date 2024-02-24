package com.serezka.telegram.session.menu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class MenuSessionConfiguration {
    final Map<String, Menu> menus = new HashMap<>();

    String initLink;

    public MenuSessionConfiguration alloc(String name, Menu menu) {
        this.menus.put(name, menu);
        return this;
    }

    public MenuSessionConfiguration onInit(String initLink) {
        this.initLink = initLink;
        return this;
    }

    public static MenuSessionConfiguration create() {
        return new MenuSessionConfiguration();
    }
}
