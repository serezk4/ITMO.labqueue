package com.serezka.telegram.session.menu;

import java.util.List;

public record PageResponse(String text, List<PageButton> buttons, int columns) {
    public void mapButtons(long sessionId) {
        buttons.forEach(row -> row.getCallbackBundle().info().addFirst(String.valueOf(sessionId)));
    }
}
