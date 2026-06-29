/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.content;

import dev.portablemc.api.PortableIdentifier;
import java.util.Objects;

/**
 * Portable declaration for a basic item or block item.
 *
 * @param id registry id
 * @param settings portable item settings
 * @param blockItem whether this item represents the block with the same id
 */
public record PortableItemDefinition(PortableIdentifier id, PortableItemSettings settings, boolean blockItem) {
    /**
     * Creates an item declaration.
     */
    public PortableItemDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(settings, "settings");
    }
}
