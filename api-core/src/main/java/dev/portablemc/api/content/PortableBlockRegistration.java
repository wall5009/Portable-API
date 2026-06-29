/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.content;

import java.util.Objects;

/**
 * Pair of handles returned by simple block registration.
 *
 * @param block handle for the block
 * @param item handle for the generated block item
 */
public record PortableBlockRegistration(
        PortableRegistryHandle<PortableBlockDefinition> block,
        PortableRegistryHandle<PortableItemDefinition> item
) {
    /**
     * Creates a block registration result.
     */
    public PortableBlockRegistration {
        Objects.requireNonNull(block, "block");
        Objects.requireNonNull(item, "item");
    }
}
