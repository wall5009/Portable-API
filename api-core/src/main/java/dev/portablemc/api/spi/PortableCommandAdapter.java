/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.spi;

import dev.portablemc.api.command.PortableCommand;
import dev.portablemc.api.command.PortableCommandTree;

/**
 * Service-provider interface used by loader modules to receive command declarations.
 */
public interface PortableCommandAdapter {
    /**
     * Registers a command declaration with the active loader bridge.
     *
     * @param command command declaration
     */
    void register(PortableCommand command);

    /**
     * Registers a full portable command tree. Adapters that have not yet grown
     * tree support preserve v1.0 behavior for a single executable root literal.
     *
     * @param tree command tree
     */
    default void registerTree(final PortableCommandTree tree) {
        if (tree.root().children().isEmpty() && tree.root().executor().isPresent()) {
            register(new PortableCommand(tree.root().name(), tree.root().permissionLevel(), tree.root().executor().orElseThrow()));
            return;
        }
        throw new UnsupportedOperationException("Portable command tree registration is not implemented by this adapter");
    }
}
