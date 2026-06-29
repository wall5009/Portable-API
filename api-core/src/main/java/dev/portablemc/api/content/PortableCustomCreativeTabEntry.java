/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.content;

import dev.portablemc.api.PortableIdentifier;
import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;
import java.util.Objects;

/**
 * Portable request to add an item to a custom creative tab.
 *
 * @param tabId custom tab id
 * @param itemId item registry id
 */
@PublicApi
@Since("1.1.0")
public record PortableCustomCreativeTabEntry(PortableIdentifier tabId, PortableIdentifier itemId) {
    /**
     * Creates a custom creative-tab entry.
     */
    public PortableCustomCreativeTabEntry {
        Objects.requireNonNull(tabId, "tabId");
        Objects.requireNonNull(itemId, "itemId");
    }
}
