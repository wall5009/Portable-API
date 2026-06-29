/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api;

/**
 * Entry point implemented by shared mod code.
 *
 * <p>Loader-specific entrypoints should be intentionally thin: construct the
 * shared mod object and pass it to the matching Portable API bootstrap class.
 * The bootstrap creates a {@link PortableModContext} whose services are backed
 * by the active loader and Minecraft version.</p>
 */
@FunctionalInterface
public interface PortableMod {
    /**
     * Called exactly once by a loader bootstrap during mod construction or
     * initialization. Register content, commands, config hooks, lifecycle
     * callbacks, and protocol declarations here.
     *
     * @param context loader-backed context for this mod
     */
    void initialize(PortableModContext context);
}
