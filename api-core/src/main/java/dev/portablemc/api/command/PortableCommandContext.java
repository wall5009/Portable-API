/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.command;

import dev.portablemc.api.PortableIdentifier;
import java.util.Optional;

/**
 * Loader-neutral command execution context.
 */
public interface PortableCommandContext {
    /**
     * Sends a plain-text system message to the command source.
     *
     * @param message message text
     */
    void reply(String message);

    /**
     * Sends a plain-text failure message to the command source. The default
     * implementation uses the same feedback channel as {@link #reply(String)}.
     *
     * @param message failure message
     */
    default void fail(final String message) {
        reply(message);
    }

    /**
     * Returns the source display name when available.
     *
     * @return optional source name
     */
    default Optional<String> sourceName() {
        return Optional.empty();
    }

    /**
     * Returns whether the source has a vanilla permission level.
     *
     * @param level permission level from 0 through 4
     * @return whether the source has the permission
     */
    default boolean hasPermission(final int level) {
        return false;
    }

    /**
     * Returns a typed argument value.
     *
     * @param name argument name
     * @param type expected type
     * @param <T> argument value type
     * @return argument value
     */
    default <T> T argument(final String name, final Class<T> type) {
        throw new IllegalArgumentException("No portable command argument named " + name + " is available");
    }

    /**
     * Returns a portable identifier argument.
     *
     * @param name argument name
     * @return identifier value
     */
    default PortableIdentifier identifierArgument(final String name) {
        return argument(name, PortableIdentifier.class);
    }
}
