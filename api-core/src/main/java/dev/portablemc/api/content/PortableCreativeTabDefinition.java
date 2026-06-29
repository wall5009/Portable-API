/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.content;

import dev.portablemc.api.PortableIdentifier;
import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;
import java.util.Objects;

/**
 * Portable declaration for a simple custom creative tab.
 *
 * @param id tab id
 * @param translationKey tab title translation key
 * @param iconItem item id used as the tab icon
 */
@PublicApi
@Since("1.1.0")
public record PortableCreativeTabDefinition(
        PortableIdentifier id,
        String translationKey,
        PortableIdentifier iconItem
) {
    /**
     * Creates a custom creative-tab declaration.
     */
    public PortableCreativeTabDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(translationKey, "translationKey");
        Objects.requireNonNull(iconItem, "iconItem");
        if (translationKey.isBlank()) {
            throw new IllegalArgumentException("translationKey must not be blank");
        }
    }
}
