package com.serezka.telegram.session.menu;

import com.serezka.telegram.util.keyboard.type.Inline;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.meta.api.objects.CallbackBundle;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class Page {
    protected static long idCounter = 0;

    private final long id = idCounter++;

    String text = "";
    final List<Inline.Button> buttons = new ArrayList<>();
    int rowSize = 3;

    public Page(String text) {
        this.text = text;
    }

    public Page setRowSize(int rowSize) {
        this.rowSize = rowSize;
        return this;
    }

    public Page addButton(String text) {
        addButtonWithData(text, text);
        return this;
    }

    public Page addButtonWithData(String text, String data) {
        addButton(text, data, "this");
        return this;
    }

    public Page addButtonWithLink(String text, String link) {
        addButton(text, text, link);
        return this;
    }

    public Page addButton(String text, String data, String link) {
        buttons.add(new Inline.Button(text, new CallbackBundle(List.of(link), List.of(data))));
        return this;
    }
}
