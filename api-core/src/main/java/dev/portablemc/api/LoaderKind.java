/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api;

/**
 * The mod loader family that is hosting a Portable API runtime.
 *
 * <p>This enum is deliberately small and literal. V1 supports Fabric and Forge
 * on Minecraft 1.20.1, and Fabric and NeoForge on Minecraft 1.21.1. Code that
 * needs behavior outside that matrix should branch explicitly instead of asking
 * Portable API to pretend the behavior is portable.</p>
 */
public enum LoaderKind {
    /** Fabric Loader. */
    FABRIC,

    /** MinecraftForge/FML, supported here only for Minecraft 1.20.1. */
    FORGE,

    /** NeoForge/FML, supported here only for Minecraft 1.21.1. */
    NEOFORGE
}
