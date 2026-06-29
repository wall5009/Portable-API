/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.command;

import dev.portablemc.api.spi.PortableCommandAdapter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Registers portable commands.
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

    /**
     * Registers a portable command tree rooted at a literal node.
     *
     * @param tree command tree
     */
    public void registerTree(final PortableCommandTree tree) {
        Objects.requireNonNull(tree, "tree");
        if (!names.add(tree.root().name())) {
            throw new IllegalStateException("Duplicate command registration for /" + tree.root().name());
        }
        adapter.registerTree(tree);
    }
}
