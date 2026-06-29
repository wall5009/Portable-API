/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.command;

/**
 * Executes a portable literal command.
 */
@FunctionalInterface
public interface PortableCommandExecutor {
    /**
     * Runs the command.
     *
     * @param context command context
     * @return Brigadier-compatible result code
     */
    int run(PortableCommandContext context);
}
