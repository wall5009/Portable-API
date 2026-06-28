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

import dev.portablemc.api.spi.PortableCommandAdapter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Registers portable literal commands.
 */
public final class PortableCommandManager {
    private final PortableCommandAdapter adapter;
    private final Set<String> names = new HashSet<>();

    /**
     * Creates a command manager.
     *
     * @param adapter loader adapter
     */
    public PortableCommandManager(final PortableCommandAdapter adapter) {
        this.adapter = Objects.requireNonNull(adapter, "adapter");
    }

    /**
     * Registers a root literal command.
     *
     * @param name root command literal
     * @param permissionLevel vanilla permission level from 0 to 4
     * @param executor command executor
     */
    public void registerLiteral(final String name, final int permissionLevel, final PortableCommandExecutor executor) {
        if (!names.add(Objects.requireNonNull(name, "name"))) {
            throw new IllegalStateException("Duplicate command registration for /" + name);
        }
        adapter.register(new PortableCommand(name, permissionLevel, executor));
    }
}
