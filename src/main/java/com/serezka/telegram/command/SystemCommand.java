package com.serezka.telegram.command;

import com.serezka.database.model.telegram.TelegramUser;

import java.util.List;

/**
 * Abstract class for system commands
 * For commands like help e.t.c.
 * @version 1.0
 * @see Command
 */
public abstract class SystemCommand extends Command {
    public SystemCommand(List<String> usage, String help) {
        super(usage, help, TelegramUser.Role.MIN);
    }
}
