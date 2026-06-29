/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.command;

import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;
import java.util.Objects;

/**
 * Portable command tree rooted at a literal command.
 *
 * @param root root literal node
 */
@PublicApi
@Since("1.1.0")
public record PortableCommandTree(PortableCommandNode root) {
    /**
     * Validates the command tree.
     */
    public PortableCommandTree {
        Objects.requireNonNull(root, "root");
        if (!root.literal()) {
            throw new IllegalArgumentException("Portable command roots must be literal nodes: " + root.name());
        }
    }

    /**
     * Starts a root literal builder.
     *
     * @param name root literal
     * @return node builder
     */
    public static PortableCommandNode.Builder literal(final String name) {
        return PortableCommandNode.literal(name);
    }
}
