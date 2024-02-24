package com.serezka.telegram.session.menu;

import com.serezka.telegram.util.keyboard.type.Inline;
import org.telegram.telegrambots.meta.api.objects.CallbackBundle;

import java.util.Arrays;
import java.util.Collections;

public class PageButton extends Inline.Button {
    public PageButton(String text, String... params) {
        super(text, new CallbackBundle(Collections.emptyList(), Arrays.stream(params).toList()));
    }
}
