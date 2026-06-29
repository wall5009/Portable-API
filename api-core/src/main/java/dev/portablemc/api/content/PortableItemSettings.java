/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.content;

/**
 * Small, stable subset of item properties.
 *
 * @param maxStackSize maximum stack size, from 1 through 64
 * @param fireResistant whether the item should survive fire and lava
 */
public record PortableItemSettings(int maxStackSize, boolean fireResistant) {
    /**
     * Validates portable item settings.
     */
    public PortableItemSettings {
        if (maxStackSize < 1 || maxStackSize > 64) {
            throw new IllegalArgumentException("maxStackSize must be between 1 and 64: " + maxStackSize);
        }
    }

    /**
     * Returns default item settings.
     *
     * @return default settings
     */
    public static PortableItemSettings defaults() {
        return new PortableItemSettings(64, false);
    }

    /**
     * Returns a copy with a new stack size.
     *
     * @param value stack size from 1 through 64
     * @return updated settings
     */
    public PortableItemSettings stacksTo(final int value) {
        return new PortableItemSettings(value, fireResistant);
    }

    /**
     * Returns a copy that is fire resistant.
     *
     * @return updated settings
     */
    public PortableItemSettings asFireResistant() {
        return new PortableItemSettings(maxStackSize, true);
    }
}
