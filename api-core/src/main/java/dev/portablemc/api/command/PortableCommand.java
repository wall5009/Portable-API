/*
 * MIT License
 *
 * Copyright (c) 2026 PortableMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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
