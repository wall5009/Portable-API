/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api;

/**
 * Physical side of the currently running process.
 *
 * <p>This is not a logical-side abstraction. A dedicated server reports
 * {@link #DEDICATED_SERVER}; an integrated server inside a client reports
 * {@link #CLIENT} because client-only classes are present in that process.</p>
 */
public enum RuntimeSide {
    /** A Minecraft client process, including single-player integrated server runs. */
    CLIENT,

    /** A headless dedicated server process. */
    DEDICATED_SERVER
}
