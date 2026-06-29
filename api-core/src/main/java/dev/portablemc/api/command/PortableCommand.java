/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.command;

import java.util.Objects;

/**
 * Portable literal command declaration.
 *
 * @param name root literal name
 * @param permissionLevel vanilla permission level required to execute
 * @param executor command body
 */
public record PortableCommand(String name, int permissionLevel, PortableCommandExecutor executor) {
    /**
     * Validates a command declaration.
     */
    public PortableCommand {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(executor, "executor");
        if (!name.matches("[a-z0-9_./-]+")) {
            throw new IllegalArgumentException("Command name must be lowercase and contain no spaces: " + name);
        }
        if (permissionLevel < 0 || permissionLevel > 4) {
            throw new IllegalArgumentException("permissionLevel must be between 0 and 4: " + permissionLevel);
        }
    }
}
