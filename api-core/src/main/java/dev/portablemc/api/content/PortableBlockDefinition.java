/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.content;

import dev.portablemc.api.PortableIdentifier;
import java.util.Objects;

/**
 * Portable declaration for a basic block.
 *
 * @param id registry id
 * @param settings portable block settings
 */
public record PortableBlockDefinition(PortableIdentifier id, PortableBlockSettings settings) {
    /**
     * Creates a block declaration.
     */
    public PortableBlockDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(settings, "settings");
    }
}
