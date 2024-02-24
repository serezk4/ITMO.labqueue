package com.serezka.telegram.session.step;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.BiConsumer;

public interface Step extends BiConsumer<StepSession, Update> {
}
