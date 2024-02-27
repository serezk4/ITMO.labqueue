package com.serezka.telegram.updater;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.TimeUnit;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Getter
@Log4j2
public abstract class Updater implements Runnable {
    long initDelay;
    long delay;
    TimeUnit unit;

    @Override
    public final void run() {
        log.info("updater working: {} | delay: {}/{}", getClass().getSimpleName(), getDelay(), getUnit());
        update();
    }

    public abstract void update();
}
