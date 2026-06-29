/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.content;

import dev.portablemc.api.PortableIdentifier;
import java.util.Objects;

/**
 * Portable request to add an item to a vanilla creative tab.
 *
 * @param tab target tab
 * @param itemId item registry id
 */
public record PortableCreativeTabEntry(PortableCreativeTabKey tab, PortableIdentifier itemId) {
    /**
     * Creates a creative-tab entry.
     */
    public PortableCreativeTabEntry {
        Objects.requireNonNull(tab, "tab");
        Objects.requireNonNull(itemId, "itemId");
    }
}
