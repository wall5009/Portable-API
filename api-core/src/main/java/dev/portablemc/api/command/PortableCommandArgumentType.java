/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.command;

import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;

/**
 * Argument types that map to Brigadier argument semantics on every supported
 * loader target.
 */
@PublicApi
@Since("1.1.0")
public enum PortableCommandArgumentType {
    /** {@code true} or {@code false}. */
    BOOLEAN,

    /** 32-bit signed integer. */
    INTEGER,

    /** 64-bit signed integer. */
    LONG,

    /** 64-bit floating-point value. */
    DOUBLE,

    /** One non-space word. */
    WORD,

    /** Remaining input as a single string. */
    GREEDY_STRING,

    /** Minecraft-style {@code namespace:path} identifier. */
    IDENTIFIER
}
