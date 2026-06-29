/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.content;

/**
 * Small, conservative subset of block properties that maps cleanly across all
 * supported targets.
 *
 * <p>V1 does not model every block behavior. Complex blocks should expose their
 * native implementation from a version-specific common module or a loader
 * module, where the Minecraft API differences are visible to the author.</p>
 *
 * @param hardness break hardness
 * @param resistance explosion resistance
 * @param requiresCorrectTool whether the block requires the correct tool
 */
public record PortableBlockSettings(float hardness, float resistance, boolean requiresCorrectTool) {
    /**
     * Returns settings suitable for a simple stone-like demonstration block.
     *
     * @return default block settings
     */
    public static PortableBlockSettings stoneLike() {
        return new PortableBlockSettings(1.5F, 6.0F, true);
    }

    /**
     * Returns a copy with the supplied strength values.
     *
     * @param hardness block hardness
     * @param resistance explosion resistance
     * @return updated settings
     */
    public PortableBlockSettings strength(final float hardness, final float resistance) {
        return new PortableBlockSettings(hardness, resistance, requiresCorrectTool);
    }

    /**
     * Returns a copy with the correct-tool flag changed.
     *
     * @param value whether correct tool is required
     * @return updated settings
     */
    public PortableBlockSettings requiresCorrectTool(final boolean value) {
        return new PortableBlockSettings(hardness, resistance, value);
    }
}
